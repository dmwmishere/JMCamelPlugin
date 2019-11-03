package org.dmwm.jmeter.scenario;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.framework.ContextBuilder;
import org.dmwm.jmeter.sampler.CamelSampler;
import org.dmwm.jmeter.util.Serializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class SampleTest {

    private final static String CONTEXT_NAME = "jm-test-context";

    private JMeterVariables vars = new JMeterVariables();

    private JMeterContext jmCtx;

    private CamelContext cctx;

    @Before
    public void init() throws Exception {
        jmCtx = JMeterContextService.getContext();
        jmCtx.setVariables(vars);

        cctx = ContextBuilder.builder().setName(CONTEXT_NAME)
                .addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() {
                        from("direct:test").log("income = ${in.body}").transform(simple("rs-${in.body}"));
                    }
                }).build();

        cctx.start();

        vars.putObject(CONTEXT_NAME, cctx);
    }

    @After
    public void shutdown() throws Exception {
        cctx.stop();
    }


    @Test
    public void test_00_0_sample() {

        CamelSampler sampler = new CamelSampler();

        sampler.setCamelContextName(CONTEXT_NAME);
        sampler.setDirectName("direct:test");
        sampler.setBody("test body string");
        sampler.setConverterClass("None");
        sampler.setSaveResultAs(true);

        sampler.threadStarted();

        SampleResult res = sampler.sample(new Entry());

        assertThat(new String(res.getResponseData()), startsWith("rs-"));
    }

    @Test
    public void test_01_0_converter() throws Exception {

        CamelSampler sampler = new CamelSampler();

        sampler.setCamelContextName(CONTEXT_NAME);
        sampler.setDirectName("direct:test");
        sampler.setBody("test body string");
        sampler.setConverterClass("org.dmwm.jmeter.test.SamplerTestConverter");
        sampler.setSaveResultAs(true);

        sampler.threadStarted();

        SampleResult res = sampler.sample(new Entry());

        assertThat(new String(res.getResponseData()), startsWith("rs-test-converted-"));
    }

    @Test
    public void test_02_0_multiple_samplers() throws Exception {

        final int THREAD_COUNT = 10;

        ExecutorService srv = Executors.newFixedThreadPool(THREAD_COUNT);

        List<Callable<List<Integer>>> callables = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            callables.add(new TestThreadSampler(CONTEXT_NAME,
                    "direct:test", "None", 20));
        }

        List<Future<List<Integer>>> futures = srv.invokeAll(callables);

        srv.shutdown();

        for (Future<List<Integer>> future : futures) {
            assertThat(future.get(), not(hasItems(0)));
        }

    }

    @Test
    public void test_03_0_save_as() throws Exception {
        CamelSampler sampler = new CamelSampler();

        sampler.setCamelContextName(CONTEXT_NAME);
        sampler.setDirectName("direct:test");
        sampler.setBody("test body bytes");
        sampler.setConverterClass("org.dmwm.jmeter.test.SamplerTestConverter");
        sampler.setSaveResultAs(false);

        sampler.threadStarted();

        SampleResult res = sampler.sample(new Entry());

        Object value = Serializer.deserialize(res.getResponseData());

        assertThat(value, instanceOf(String.class));

        assertThat(value.toString(), equalTo("rs-test-converted-test body bytes"));
    }

    class TestThreadSampler implements Callable<List<Integer>> {
        private final CamelSampler sampler = new CamelSampler();

        private final List<Integer> rsChecks = new ArrayList<>();

        private final int iterationCount;

        TestThreadSampler(String contextName, String directName, String converter, int iterationCount) {
            sampler.setCamelContextName(contextName);
            sampler.setDirectName(directName);
            sampler.setConverterClass(converter);
            sampler.threadStarted();
            sampler.setSaveResultAs(true);
            this.iterationCount = iterationCount;

        }

        @Override
        public List<Integer> call() {

            JMeterContextService.getContext().setVariables(vars);

            final String threadName = Thread.currentThread().getName();

            for (int i = 0; i < iterationCount; i++) {
                String uuid = UUID.randomUUID().toString();
                sampler.setBody(threadName + "-" + uuid);
                SampleResult res = sampler.sample(new Entry());
                if (!res.getResponseDataAsString().equals("rs-" + threadName + "-" + uuid)) {
                    System.out.println("Fail " + threadName + " at " + i + ", uuid = " + uuid + ", response is '" + res.getResponseDataAsString() + "'");
                    rsChecks.add(0);
                } else {
                    rsChecks.add(1);
                }
            }
            return rsChecks;
        }
    }

}
