package org.dmwm.jmeter.framework.invalid;

import org.dmwm.jmeter.framework.JCBean;

public class ExceptionInMethodCall {

    @JCBean("exception-bean")
    public Object getException() {
        throw new RuntimeException("BEAN SHOULD NOT BE CREATED!");
    }
}
