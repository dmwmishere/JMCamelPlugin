package org.dmwm.jmeter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.impl.SimpleRegistry;

@Slf4j
@UtilityClass
public class CamelContextUtils {

    public String initBean(String name, String className, SimpleRegistry registry) {
        try {
            if(registry.containsKey(name)){
                log.error("Bean " + name + " already defined in registry as "
                        + registry.get(name).getClass());
                return null;
            } else {
                Class<?> clazz = Class.forName(className);
                Object instance = clazz.newInstance();
                registry.put(name, instance);
                return instance.toString();
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            log.error("Failed to initialize bean " + name + ": " + e.getClass() + " - " + e.getMessage());
            return null;
        }
    }



}
