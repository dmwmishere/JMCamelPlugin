package org.dmwm.jmeter.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.jmeter.threads.JMeterContext;
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.framework.JCBean;
import org.dmwm.jmeter.framework.PicoRegistry;
import org.dmwm.jmeter.framework.converter.Converter;
import org.picocontainer.MutablePicoContainer;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class CamelContextUtils {

    /**
     * class path to scan for {@link JCBean}
     */
    private static final String BEAN_CLASS_PATH = "bean_class_path";

    public void initBean(String name, String className, MutablePicoContainer picoContainer) {
        try {
            Class<?> clazz = Class.forName(className);
            picoContainer.addComponent(name, clazz);
        } catch (ClassNotFoundException e) {
            log.error("Failed to initialize bean " + name + ": " + e.getClass() + " - " + e.getMessage());
        }
    }

    /**
     * get jmeter properties from context and fill camel properties component
     * @param context jmeter context to get properties from
     * @return camel properties component
     */
    public PropertiesComponent initProperties(JMeterContext context) {
        PropertiesComponent pc = new PropertiesComponent();

        Properties props = new Properties(context.getProperties());

        context.getVariables().entrySet().forEach(entry -> {
            log.debug("Variable " + entry.getKey() + " = " + entry.getValue().toString());
            props.setProperty(entry.getKey(), entry.getValue().toString());
        });

        pc.setInitialProperties(props);
        return pc;
    }

    /**
     * convert java properties into camel properties component
     * @param properties java properties
     * @return camel properties component
     */
    public PropertiesComponent initProperties(Properties properties) {
        PropertiesComponent pc = new PropertiesComponent();
        pc.setInitialProperties(properties);
        return pc;
    }

    /**
     * get converter instance from class string
     * @param converterClass fully qualified class name
     * @param <T> type to convert to
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Converter<T> initConverter(String converterClass) {
        Converter<T> converter = null;
        try {
            if (!converterClass.equals("None")) {
                converter = (Converter) Class.forName(converterClass).newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("Failed to create converter type of {} because {}: {}", converterClass, e.getClass().getSimpleName(), e.getMessage());
        }
        return converter;
    }

    public PicoRegistry initRegistry(@NonNull JMeterVariables jmvars) {
        String[] classPaths = Optional.ofNullable(jmvars.get(BEAN_CLASS_PATH)).orElse(
                System.getProperty(BEAN_CLASS_PATH, "org.dmwm.jmeter.beans")
        ).split(":");
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

        for (Map.Entry<Class<?>, List<Method>> entry : classMethods.entrySet()) {
            try {
                if (entry.getKey().getConstructor().getParameterCount() > 0) {
                    log.error(entry.getKey() + ": constructor parameters not allowed in config class!");
                }
                Object configInstance = entry.getKey().newInstance();
                for (Method method : entry.getValue()) {
                    try {
                        Object bean = getBeanFromMethod(method, configInstance);
                        if (bean != null) {
                            registry.addComponent(method.getAnnotation(JCBean.class).value(),
                                    bean);
                        }
                    } catch (InvocationTargetException e) {
                        log.error("Failed to inialize bean " + method.getAnnotation(JCBean.class).value() +
                                " typeof " + method.getReturnType().getSimpleName() + " due to: " + e.getMessage(), e);
                    }
                }
            } catch (IllegalAccessException | InstantiationException | NoSuchMethodException e) {
                log.error("Failed to initialize config " + entry.getKey().getSimpleName() +
                        " due to: " + e.getMessage(), e);
            }
        }
        return registry;
    }

    private Object getBeanFromMethod(Method method, Object instance) throws InvocationTargetException, IllegalAccessException {
        if (method.getParameterCount() > 0) {
            log.error("Error, method " + method.getDeclaringClass().getSimpleName() + "::" + method.getName() + " has " + method.getParameterCount() + " parameters!");
            return null;
        }
        return method.invoke(instance);

    }

}
