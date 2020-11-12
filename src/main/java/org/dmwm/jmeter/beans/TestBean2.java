package org.dmwm.jmeter.beans;

import lombok.extern.slf4j.Slf4j;
import org.dmwm.jmeter.framework.JCBean;

@Slf4j
@JCBean("test3")
public class TestBean2 implements TestInterface {

    @Override
    public void printStr() {
        log.warn("PRINT STRING BEAN " + this.getClass().getName());
    }

}
