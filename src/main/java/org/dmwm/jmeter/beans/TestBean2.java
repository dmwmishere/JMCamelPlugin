package org.dmwm.jmeter.beans;

import org.dmwm.jmeter.framework.JCBean;

@JCBean("test3")
public class TestBean2 implements TestInterface {

    @Override
    public void printStr(){
        System.out.println("PRINT STRING BEAN " + this.getClass().getName());
    }

}
