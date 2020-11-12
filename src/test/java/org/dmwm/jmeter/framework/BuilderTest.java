package org.dmwm.jmeter.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.dmwm.jmeter.test.CamelContextStartedAssertListener;
import org.dmwm.jmeter.util.CamelContextUtils;
import org.junit.Test;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BuilderTest {

    private CamelContextStartedAssertListener cctxSAL = new CamelContextStartedAssertListener();

    private Builder builder = ContextBuilder.builder();

    @Test
    public void test_00_0_build_start() {
        cctxSAL.startContext(builder.build());
        cctxSAL.assertContextStarted();
    }


    @Test
    public void test_00_1_addRoute() throws Exception {
        cctxSAL.startContext(builder.addRoutes(new RouteBuilder() {
            @Override
            public void configure() {
                from("direct:test").log("test");
            }
        }).build());
        cctxSAL.assertContextStarted();
    }

    @Test
    public void test_01_0_init_properties() {

        JMeterContext jmc = JMeterContextService.getContext();
        JMeterUtils.loadJMeterProperties(ClassLoader.getSystemResource("jmeter.properties").getFile());
        jmc.getProperties().setProperty("sampleProperty", "sample prop");
        JMeterVariables jmv = new JMeterVariables();
        jmv.put("sampleVariable", "sample text");
        jmc.setVariables(jmv);
        PropertiesComponent pc = CamelContextUtils.initProperties(jmc);
        assertThat(pc.getInitialProperties().getProperty("sampleVariable"), equalTo("sample text"));
        assertThat(pc.getInitialProperties().getProperty("sampleProperty"), equalTo("sample prop"));
    }

    @Test
    public void test_01_1_set_properties() throws Exception {
        Properties props = new Properties();
        props.setProperty("routeName", "qwerty");
        CamelContext cctx = builder
                .setProperties(props)
                .addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() {
                        from("direct:{{routeName}}").log("test");
                    }
                }).build();
        cctxSAL.startContext(cctx);
        cctxSAL.assertContextStarted();
    }

    @Test
    public void test_02_0_set_registry() {
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("testBean", "sample text");
        CamelContext cctx = builder
                .setRegistry(registry)
                .build();
        cctxSAL.startContext(cctx);
        cctxSAL.assertContextStarted();
        assertThat(cctx.getRegistry().lookupByName("testBean").toString(), equalTo("sample text"));
    }

    @Test
    public void test_03_0_complex() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.put("testProcessor", (Processor) exchange -> {
            String exchangeText = exchange.getIn().getBody(String.class);
            if(!exchangeText.equals("input"))
                throw new IllegalStateException("Invalid exchange received: " + exchangeText);
        });
        Properties props = new Properties();
        props.setProperty("routeName", "qwerty");
        CamelContext cctx = builder
                .setName("testContext")
                .setProperties(props)
                .addRoutes(new RouteBuilder() {
                    @Override
                    public void configure() {
                        from("direct:{{routeName}}").process("testProcessor");
                    }
                })
                .setRegistry(registry)
                .build();

        cctxSAL.startContext(cctx);
        cctxSAL.assertContextStarted();

        assertThat(cctx.getName(), equalTo("testContext"));

        cctx.createFluentProducerTemplate()
                .to("direct:".concat(props.getProperty("routeName"))).withBody("input")
                .asyncRequest().get(1000, TimeUnit.MILLISECONDS);
    }
}
