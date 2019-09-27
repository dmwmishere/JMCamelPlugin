package org.dmwm.jmeter.config;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;
import org.dmwm.jmeter.data.ExchangeSettingPair;
import org.dmwm.jmeter.data.RegistryTableElement;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;

public class ExchangeConfigElementBeanInfo extends BeanInfoSupport {

    public ExchangeConfigElementBeanInfo() {
        super(ExchangeConfigElement.class);
        createPropertyGroup("exchangeParameters", new String[]{
                "exchangeProperties", "exchangeHeaders"});

        PropertyDescriptor p = property("exchangeProperties");
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
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, new ArrayList<ExchangeSettingPair>());
        p.setValue(MULTILINE, Boolean.TRUE);

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
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, new ArrayList<ExchangeSettingPair>());
        p.setValue(MULTILINE, Boolean.TRUE);

    }
}
