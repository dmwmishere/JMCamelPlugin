package org.dmwm.jmeter.config;

import org.apache.camel.CamelContext;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.apache.jmeter.util.JMeterUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CamelConfigElementTest {

    private final static String CONTEXT_NAME = "jm-test-context";

    private JMeterVariables vars = new JMeterVariables();
    private JMeterContext jmCtx;

    public CamelConfigElementTest() {
        JMeterUtils.loadJMeterProperties(
                ClassLoader.getSystemResource("jmeter.properties").getFile());
        JMeterUtils.initLocale();
    }

    @Before
    public void init() {
        jmCtx = JMeterContextService.getContext();
        jmCtx.setVariables(vars);
    }

    @Test
    public void test_00_0_config_start_stop() {

        CamelConfigElement cce = new CamelConfigElement();
        cce.setProperty("contextName", CONTEXT_NAME);

        cce.setProperty("routeXml",
                "<routes xmlns=\"http://camel.apache.org/schema/spring\">" +
                        "    <route>" +
                        "        <from uri=\"direct:test\"/>" +
                        "        <log message=\"received ${in.body}\" />" +
                        "    </route>" +
                        "</routes>");

        cce.setThreadContext(jmCtx);

        cce.testStarted();

        CamelContext cctx = (CamelContext) vars.getObject(CONTEXT_NAME);

        assertThat(cctx.getStatus().isStarted(), equalTo(true));

        cce.testEnded();

        assertThat(cctx.getStatus().isStarted(), equalTo(false));
    }

    @Test
    public void test_01_0_load_scanned_bean() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.test");
        CamelConfigElement cce = new CamelConfigElement();
        cce.setProperty("contextName", CONTEXT_NAME);
        cce.setProperty("routeXml", "<routes xmlns=\"http://camel.apache.org/schema/spring\"></routes>");

        cce.setThreadContext(jmCtx);

        cce.testStarted();

        CamelContext cctx = (CamelContext) vars.getObject(CONTEXT_NAME);

        assertThat(cctx.getRegistry().lookupByName("test-bean"), notNullValue());

        assertThat(cctx.getStatus().isStarted(), equalTo(true));
        cce.testEnded();
        assertThat(cctx.getStatus().isStarted(), equalTo(false));
    }

}
