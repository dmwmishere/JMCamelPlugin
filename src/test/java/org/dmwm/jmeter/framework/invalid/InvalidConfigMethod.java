package org.dmwm.jmeter.framework.invalid;

import org.dmwm.jmeter.framework.JCBean;

public class InvalidConfigMethod {

    @JCBean("invalid-method")
    public String getString(int i){
        return "qwerty";
    }

}
