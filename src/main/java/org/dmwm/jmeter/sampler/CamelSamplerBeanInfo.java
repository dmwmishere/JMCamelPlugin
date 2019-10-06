package org.dmwm.jmeter.sampler;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;
import org.dmwm.jmeter.data.ExchangeSettingPair;

import java.beans.PropertyDescriptor;

public class CamelSamplerBeanInfo extends BeanInfoSupport {

    public CamelSamplerBeanInfo() {
        super(CamelSampler.class);

        createPropertyGroup("samplerConfig", new String[]{
                "camelContextName", "directName", "converterClass", "body"});

        PropertyDescriptor p = property("camelContextName", TypeEditor.ComboStringEditor);
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

        createPropertyGroup("exchangeConfig", new String[]{
                "exchangeHeaders"
        });

        p = property("exchangeHeaders");
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, ExchangeSettingPair.class.getName());
        p.setValue(TableEditor.HEADERS, new String[]{
                "Property name",
                "value"
        });
        p.setValue(TableEditor.OBJECT_PROPERTIES,
                new String[]{
                        ExchangeSettingPair.EXCHANGE_SETTING_NAME,
                        ExchangeSettingPair.EXCHANGE_SETTING_VALUE
                });

        //FIXME: explore why IllegalArgumentException occures if this is true and according context config classes table is empty
        //org.dmwm.jmeter.sampler.CamelSampler#setExchangeHeaders(String  )
        //Caused by: java.lang.IllegalArgumentException: argument type mismatch
        p.setValue(NOT_UNDEFINED, Boolean.FALSE);
        p.setValue(MULTILINE, Boolean.TRUE);

    }

}
