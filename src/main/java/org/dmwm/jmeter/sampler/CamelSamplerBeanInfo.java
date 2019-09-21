package org.dmwm.jmeter.sampler;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TypeEditor;

import java.beans.PropertyDescriptor;

public class CamelSamplerBeanInfo extends BeanInfoSupport {

    public CamelSamplerBeanInfo() {
        super(CamelSampler.class);

        createPropertyGroup("samplerConfig", new String[]{
                "contextName", "directName", "converterClass", "body"});

        PropertyDescriptor p = property("contextName", TypeEditor.ComboStringEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "jm-camel-context-1");

        p = property("directName", TypeEditor.ComboStringEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "direct:test");

        p = property("converterClass", TypeEditor.ComboStringEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "None");

        p = property("body", TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "test body");

    }


}
