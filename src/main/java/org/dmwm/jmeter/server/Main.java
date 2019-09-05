package org.dmwm.jmeter.server;

import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
import org.apache.jmeter.rmi.RmiUtils;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;

public class Main {

    private static final String JMETER_HOME = System.getProperty("JMETER_HOME");

    public static void main(String[] args) throws Exception {

        SingletonCamelContext.instance();

        JMeterUtils.loadJMeterProperties(JMETER_HOME + "/bin/jmeter.properties");
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.initLocale();

        SaveService.loadProperties();

        RemoteJMeterEngineImpl.startServer(RmiUtils.getRmiRegistryPort());
    }

}
