package org.dmwm.jmeter.server;

import com.helger.commons.io.stream.StringInputStream;
import org.apache.jmeter.engine.RemoteJMeterEngineImpl;
import org.apache.jmeter.rmi.RmiUtils;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.dmwm.jmeter.framework.Builder;
import org.dmwm.jmeter.framework.ContextBuilder;
import org.dmwm.jmeter.util.CamelContextUtils;

import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


/**
 * Run plugin as standalone jmeter-server.
 * Command properties:
 *  JMETER_HOME - absolute path to jmeter distribution
 *  jc_context_name - camel context name (default is jmeter-camel-context)
 *  jc_context_route_definition - camel route definition xml file
 *  bean_class_path - scan for classes in extension
 * Command sample:
 * java -DJMETER_HOME=/apache-jmeter-5.0 -Djava.rmi.server.hostname=127.0.0.1
 * -Djc_context_route_definition=./routesXml.xml
 * -cp ../*:./* org.dmwm.jmeter.server.Main --testPropertyValue=qwerty --jc_context_name=SAMPLE
 */
public class Main {

    private static final String JMETER_HOME = System.getProperty("JMETER_HOME");

    public static void main(String[] args) throws Exception {

        SingletonCamelContext.instance(init(System.getProperties(), Arrays.stream(args).filter(argument ->
                argument.startsWith("--")).map(argument -> argument.substring(2)).collect(Collectors.toList())));

        JMeterUtils.loadJMeterProperties(JMETER_HOME + "/bin/jmeter.properties");
        JMeterUtils.setJMeterHome(JMETER_HOME);
        JMeterUtils.initLocale();
        SaveService.loadProperties();

        RemoteJMeterEngineImpl.startServer(RmiUtils.getRmiRegistryPort());
    }

    private static Builder init(Properties properties, List<String> argumentProperties) throws Exception {
        String routeXml = properties.getProperty("jc_context_route_definition");
        Properties props = new Properties(JMeterUtils.getProperties(JMETER_HOME + "/bin/jmeter.properties"));
        argumentProperties.forEach(property -> {
            String[] kv = property.split("=");
            props.setProperty(kv[0], kv[1]);
        });
        return ContextBuilder.builder()
                .setName(properties.getProperty("jc_context_name", "jmeter-camel-context"))
                .setProperties(props)
                .setRegistry(CamelContextUtils.initRegistry(null))
                .addRoutes(routeXml == null
                        ? new StringInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<routes xmlns=\"http://camel.apache.org/schema/spring\">\n" +
                        "    <route id=\"timerRoute\">\n" +
                        "        <from uri=\"timer:noRouteNotifTimer?delay=1000&amp;repeatCount=0\"/>\n" +
                        "        <log message=\"NO ROUTES DEFINED! USE jc_context_route_definition\" loggingLevel=\"WARN\"/>\n" +
                        "    </route>\n" +
                        "</routes>", Charset.defaultCharset())
                        : new FileInputStream(routeXml));
    }

}
