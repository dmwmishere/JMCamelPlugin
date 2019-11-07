package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;
import org.picocontainer.annotations.Inject;

@JCBean("FF")
public class F {

    @Inject
    private E e;

    private final A a;

    public F(A a) {
        this.a = a;
    }

    public E getE() {
        return e;
    }

    public A getA() {
        return a;
    }

    @Override
    public String toString() {
        return "F[ " + e.toString() + ", " + a.toString() + " ]";
    }
}
