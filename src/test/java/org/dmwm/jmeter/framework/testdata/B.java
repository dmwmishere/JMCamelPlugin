package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;


@JCBean("BB")
public class B {

    private A a;

    public A getA() {
        return a;
    }

    public B(A a){
        System.out.println("Constructor B");
        this.a = a;
    }

    @Override
    public String toString() {
        return "B[ " + a.toString() + " ]";
    }
}
