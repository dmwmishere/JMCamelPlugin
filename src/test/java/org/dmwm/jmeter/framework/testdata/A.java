package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;

@JCBean("AA")
public class A {

    private String value;

    public String getValue() {
        return value;
    }

    public A(){
        System.out.println("Constructor A");

        value = "QWERTY";
    }

    @Override
    public String toString() {
        return "A[ " + value + " ]";
    }
}
