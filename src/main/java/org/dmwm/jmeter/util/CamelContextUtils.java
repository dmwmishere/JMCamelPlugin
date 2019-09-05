package org.dmwm.jmeter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.jmeter.threads.JMeterContext;
import org.picocontainer.MutablePicoContainer;

import java.util.Properties;

@Slf4j
@UtilityClass
public class CamelContextUtils {

    public void initBean(String name, String className, MutablePicoContainer picoContainer) {
        try {
            Class<?> clazz = Class.forName(className);
            picoContainer.addComponent(name, clazz);
        } catch (ClassNotFoundException e) {
            log.error("Failed to initialize bean " + name + ": " + e.getClass() + " - " + e.getMessage());
        }
    }

    public PropertiesComponent initProperties(JMeterContext context) {
        PropertiesComponent pc = new PropertiesComponent();

        Properties props = new Properties(context.getProperties());

        context.getVariables().entrySet().forEach(entry -> {
            System.out.println("Variable " + entry.getKey() + " = " + entry.getValue().toString());
            props.setProperty(entry.getKey(), entry.getValue().toString());
        });

        pc.setInitialProperties(props);
        return pc;
    }

    public PropertiesComponent initProperties(Properties properties){
        PropertiesComponent pc = new PropertiesComponent();
        pc.setInitialProperties(properties);
        return pc;
    }

}
