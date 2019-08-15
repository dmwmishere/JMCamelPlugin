package org.dmwm.jmeter.framework;

import org.apache.camel.spi.Registry;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PicoRegistry extends DefaultPicoContainer implements Registry {

    public PicoRegistry() {
        super(new Caching());
    }

    @Override
    public Object lookupByName(String name) {
        return getComponent(name);
    }

    @Override
    public <T> T lookupByNameAndType(String name, Class<T> type) {
        ComponentAdapter<?> comp = getComponentKeyToAdapterCache().get(name);
        if (comp != null && type.isAssignableFrom(comp.getComponentImplementation())) {
            return (T) getComponent(name);
        } else return null;
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        Map<Object, ComponentAdapter<?>> adapterCache = getComponentKeyToAdapterCache();
        Map<String, T> result = new HashMap<>();
        adapterCache.forEach((key, comp) -> {
                    if (type.isAssignableFrom(comp.getComponentImplementation())) {
                        result.put(key.toString(), (T) getComponent(key));
                    }
                }
        );
        return result;
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        return new HashSet<>(getComponents(type));
    }

    @Override
    public Object lookup(String name) {
        return lookupByName(name);
    }

    @Override
    public <T> T lookup(String name, Class<T> type) {
        return lookupByNameAndType(name, type);
    }

    @Override
    public <T> Map<String, T> lookupByType(Class<T> type) {
        return findByTypeWithName(type);
    }

    @Override
    public String toString() {
        return getComponentKeyToAdapterCache().toString();
    }
}
