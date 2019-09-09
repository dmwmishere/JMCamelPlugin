package org.dmwm.jmeter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.jmeter.threads.JMeterContext;
import org.dmwm.jmeter.framework.JCBean;
import org.dmwm.jmeter.framework.PicoRegistry;
import org.picocontainer.MutablePicoContainer;
import org.reflections.Reflections;

import java.util.Properties;
import java.util.Set;

@Slf4j
@UtilityClass
public class CamelContextUtils {

    private final static String[] CLASS_PATHS = System.getProperty("bean_class_path", "org.dmwm.jmeter.beans").split(":");

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

    public PropertiesComponent initProperties(Properties properties) {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setInitialProperties(properties);
        return pc;
    }

    public PicoRegistry initRegistry() {
        Set<Class<?>> classes = new Reflections(CLASS_PATHS).getTypesAnnotatedWith(JCBean.class);
        log.info("Found classes to add to camel context: {} from {}", classes, CLASS_PATHS);
        PicoRegistry registry = new PicoRegistry();
        classes.forEach(clazz -> registry.addComponent(clazz.getAnnotation(JCBean.class).value(), clazz));
        return registry;
    }

}
