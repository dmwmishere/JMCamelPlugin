package org.dmwm.jmeter.framework.secondpackage;

import org.dmwm.jmeter.framework.JCBean;

public class TestConfig2 {

    @JCBean("NN-m")
    public N getN(){
        return new N();
    }

}
