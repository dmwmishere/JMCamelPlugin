package org.dmwm.jmeter.beans;

import org.apache.jmeter.threads.JMeterVariables;
import org.picocontainer.annotations.Inject;

public class TestBean {

    @Inject
    private JMeterVariables vars;

    public void printStr() {
        System.out.println("PRINT STRING BEAN " + vars.get("picoProp"));
    }

}
