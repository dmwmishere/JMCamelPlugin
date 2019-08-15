package org.dmwm.jmeter.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.picocontainer.MutablePicoContainer;

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



}
