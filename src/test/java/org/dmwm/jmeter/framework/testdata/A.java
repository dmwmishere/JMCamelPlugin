package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;

@JCBean("AA")
public class A {

    private String value;

    public A() {
        System.out.println("Constructor A");

        value = "QWERTY";
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "A[ " + value + " ]";
    }
}
