package org.dmwm.jmeter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.framework.converter.Converter;
import org.dmwm.jmeter.framework.JCBean;
import org.dmwm.jmeter.framework.PicoRegistry;
import org.picocontainer.MutablePicoContainer;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

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

    public PropertiesComponent initProperties(Properties properties) {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setInitialProperties(properties);
        return pc;
    }

    public Converter initConverter(String converterClass){
        Converter<?> converter = null;
        try {
            if(!converterClass.equals("None")) {
                converter = (Converter) Class.forName(converterClass).newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("Failed to create converter type of {} because {}: {}", converterClass, e.getClass().getSimpleName(), e.getMessage());
        }
        return converter;
    }

    public PicoRegistry initRegistry(JMeterVariables jmvars) {
        String [] classPaths = System.getProperty("bean_class_path", "org.dmwm.jmeter.beans").split(":");
        Reflections refl = new Reflections(classPaths,
                new MethodAnnotationsScanner(),
                new TypeAnnotationsScanner(),
                new SubTypesScanner());

        Set<Class<?>> classes = refl.getTypesAnnotatedWith(JCBean.class);
        log.info("Found classes to add to camel context: {} from {}", classes, classPaths);
        PicoRegistry registry = new PicoRegistry();
        registry.addComponent("jmvars", jmvars);
        classes.forEach(clazz -> registry.addComponent(clazz.getAnnotation(JCBean.class).value(), clazz));

        Map<Class<?>, List<Method>> classMethods = refl.getMethodsAnnotatedWith(JCBean.class)
                .stream().collect(Collectors.groupingBy(Method::getDeclaringClass));

        log.info("Found classes in methods: {} from {}", classMethods, classPaths);

        for(Map.Entry<Class<?>, List<Method>> entry : classMethods.entrySet()) {
            try {
                if(entry.getKey().getConstructor().getParameterCount() > 0){
                    throw new IllegalArgumentException("Error, " + entry.getKey() + " constructor has parameters!");
                }
                Object configInstance = entry.getKey().newInstance();
                for (Method method : entry.getValue()) {
                    try {
                        registry.addComponent(method.getAnnotation(JCBean.class).value(),
                                getBeanFromMethod(method, configInstance));
                    } catch(InvocationTargetException e){
                        throw new RuntimeException("Failed to inialize bean " + method.getAnnotation(JCBean.class).value() +
                                " typeof " + method.getReturnType().getSimpleName() + " due to: " + e.getMessage(), e);
                    }
                }
            } catch(IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                throw new RuntimeException("Failed to initialize config " + entry.getKey().getSimpleName() +
                        " due to: " + e.getMessage(), e);
            }
        }
        return registry;
    }

    private Object getBeanFromMethod(Method method, Object instance) throws InvocationTargetException, IllegalAccessException {
        if (method.getParameterCount() > 0) {
            throw new IllegalArgumentException("Error, method " + method.getDeclaringClass().getSimpleName() + "::" + method.getName() + " has " + method.getParameterCount() + " parameters!");
        }
        return method.invoke(instance);

    }

}
