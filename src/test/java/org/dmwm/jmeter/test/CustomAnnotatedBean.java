package org.dmwm.jmeter.test;

import org.dmwm.jmeter.framework.JCBean;

@JCBean("test-bean")
public class CustomAnnotatedBean {

    public String testMethod(String input){
        return "bean-method-" + input;
    }

}
