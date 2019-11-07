package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;
import org.picocontainer.annotations.Inject;

@JCBean("EE")
public class E {
    @Inject
    B b;

    public B getB() {
        return b;
    }

    @Override
    public String toString() {
        return "E[ " + b.toString() + " ]";
    }
}
