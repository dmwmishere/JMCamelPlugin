package org.dmwm.jmeter.beans;

import lombok.extern.slf4j.Slf4j;
import org.apache.jmeter.threads.JMeterVariables;
import org.picocontainer.annotations.Inject;

@Slf4j
public class TestBean {

    @Inject
    private JMeterVariables vars;

    public void printStr() {
        log.warn("PRINT STRING BEAN " + vars.get("picoProp"));
    }

}
