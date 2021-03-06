package org.dmwm.jmeter.data;

import org.apache.jmeter.testelement.AbstractTestElement;

public class RegistryTableElement extends AbstractTestElement {

    private static final long serialVersionUID = 282040956479860029L;

    public static final String BEAN_NAME = "name";

    public static final String CLASS_NAME = "clazz";

    public static final String INSTANCE = "instance";

    public RegistryTableElement() {
        super();
    }

    @Override
    public String getName() {
        return getProperty(BEAN_NAME).getStringValue();
    }

    @Override
    public void setName(String name) {
        setProperty(BEAN_NAME, name);
    }

    public String getClazz() {
        return getProperty(CLASS_NAME).getStringValue();
    }

    public void setClazz(String clazz) {
        setProperty(CLASS_NAME, clazz);
    }

    public String getInstance() {
        return getProperty(INSTANCE).getStringValue();
    }

    public void setInstance(String instance) {
        setProperty(INSTANCE, instance);
    }
}
