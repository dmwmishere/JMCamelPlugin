package org.dmwm.jmeter.framework.invalid;

import org.dmwm.jmeter.framework.JCBean;

public class InvalidConfigConstructor {

    public InvalidConfigConstructor(int i){

    }

    @JCBean("Invalid-constructor")
    public String getInteger(){
        return "qwerty";
    }

}
