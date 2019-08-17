package org.dmwm.jmeter.test;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.IOException;

public abstract class AbstractJMeterTest extends StandardJMeterEngine {

    private static final String JMETER_HOME = System.getProperty("JMETER_HOME");

    protected HashTree testPlanTree;

    protected TestPlan plan;

    protected TestListener listener;

    public AbstractJMeterTest() throws IOException {
        JMeterUtils.loadJMeterProperties(JMETER_HOME + "/bin/jmeter.properties");
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();
    }
}
