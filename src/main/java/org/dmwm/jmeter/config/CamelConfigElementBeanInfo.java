package org.dmwm.jmeter.config;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.FileEditor;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;
import org.dmwm.jmeter.data.RegistryTableElement;

public class CamelConfigElementBeanInfo extends BeanInfoSupport {

    public CamelConfigElementBeanInfo() {
        super(CamelConfigElement.class);

        createPropertyGroup("routeConfig", new String[]{
                "contextName", "routeDefFile", "routeXml", "registryBeans"});

        PropertyDescriptor p = property("contextName", TypeEditor.ComboStringEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "jm-camel-context-1");

        p = property("routeDefFile");
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "");
        p.setValue(NOT_EXPRESSION, Boolean.TRUE);
        p.setPropertyEditorClass(FileEditor.class);

        p = property("routeXml", TypeEditor.TextAreaEditor);
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<routes xmlns=\"http://camel.apache.org/schema/spring\">\n" +
                "    <route>\n" +
                "        <from uri=\"timer:qwerty?delay=1000&amp;repeatCount=0\"/>\n" +
                "        <log message=\"TIMER TICK!!!\" />\n" +
                "    </route>\n" +
                "</routes>");
        p.setValue(TEXT_LANGUAGE, "xml");

        p = property("registryBeans");
        p.setPropertyEditorClass(TableEditor.class);
        p.setValue(TableEditor.CLASSNAME, RegistryTableElement.class.getName());
        p.setValue(TableEditor.HEADERS, new String[]{
                "Bean name",
                "Class name",
                "Instance"
        });
        p.setValue(TableEditor.OBJECT_PROPERTIES,
                new String[]{
                        RegistryTableElement.BEAN_NAME,
                        RegistryTableElement.CLASS_NAME,
                        RegistryTableElement.INSTANCE
                });
        p.setValue(NOT_UNDEFINED, Boolean.TRUE);
        p.setValue(DEFAULT, new ArrayList<RegistryTableElement>());
        p.setValue(MULTILINE, Boolean.TRUE);
    }
}
