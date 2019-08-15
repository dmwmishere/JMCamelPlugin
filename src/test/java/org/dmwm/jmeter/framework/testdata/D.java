package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;

@JCBean("DD")
public class D extends C {

    public D() {
        System.out.println("constructor D");
    }

    @Override
    public void action() {
        System.out.println("action D (@Override)");
    }

    @Override
    public String toString() {
        return "D [ ]";
    }
}
