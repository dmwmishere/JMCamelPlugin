package org.dmwm.jmeter.scenario;

import org.apache.camel.CamelContext;
import org.apache.jmeter.save.SaveService;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.JMeterThread;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import org.dmwm.jmeter.test.TestListener;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class TestCamelConfig extends StandardJMeterEngine {

    private static final String JMETER_HOME = System.getProperty("JMETER_HOME");

    private HashTree testPlanTree;
    private TestPlan plan;

    private TestListener listener;

    public TestCamelConfig() throws IOException {
        JMeterUtils.loadJMeterProperties(JMETER_HOME + "/bin/jmeter.properties");
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();
    }

    @Before
    public void init() throws IOException {
        testPlanTree = SaveService.loadTree(new File("scenario/TestPlugin.jmx"));
        plan = (TestPlan) testPlanTree.getArray()[0];
        listener = new TestListener(plan, "jm-camel-context-1");
        StandardJMeterEngine.register(listener);
    }

    @Test
    public void test_00_0_complex_run() {

        listener.addPreCheck((cctx, jmc) -> assertThat("Check context started",
                cctx.getStatus().isStarted(), equalTo(true)));

        listener.addPostCheck((cctx, jmc) -> assertThat(cctx.getStatus().isStopped(),
                equalTo(true)));

        configure(testPlanTree);
        run();

    }

//    @Test
//    public void test_00_0() {
//        CamelConfigElement cce = new CamelConfigElement();
//        cce.setRouteXml("<routes xmlns=\"http://camel.apache.org/schema/spring\">\n" +
//                "    <route>\n" +
//                "        <from uri=\"timer:qwerty?delay=1000&amp;repeatCount=0\"/>\n" +
//                "        <bean ref=\"test3\" method=\"printStr\"/>\n" +
//                "        <bean ref=\"test2\" method=\"printStr\"/>\n" +
//                "        <log message=\"TIMER TICK conf122!!!\" />\n" +
//                "    </route>\n" +
//                "    <route>\n" +
//                "    \t\t<from uri=\"direct:test\" />\n" +
//                "    \t\t<!--log message=\"DIRECT BODY: ${body}\"/-->\n" +
//                "    \t\t<transform>\n" +
//                "    \t\t\t<simple>response for ${body}</simple>\n" +
//                "    \t\t</transform>\n" +
//                "    \t\t<setHeader headerName=\"TestHeader\">\n" +
//                "        \t\t<constant>TEST+HEADER</constant>\n" +
//                "    \t\t</setHeader>\n" +
//                "    </route>\n" +
//                "</routes>");
//        cce.setContextName("test-context-1");
//        cce.setRouteDefFile("");
//        RegistryTableElement rte = new RegistryTableElement();
//        rte.setName("test2");
//        rte.setClazz("org.dmwm.jmeter.beans.TestBean");
//        cce.setRegistryBeans(Collections.singletonList(rte));
//        cce.testStarted();
//        assertThat(cce.getCctx().getStatus().isStarted(), equalTo(true));
//    }
}
