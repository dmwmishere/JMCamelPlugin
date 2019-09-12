package org.dmwm.jmeter.framework.testdata;

import org.dmwm.jmeter.framework.JCBean;

public class TestConfig {

    String testVar = "testVarrrr";

    @JCBean("AA-m")
    public A getA() {
        System.out.println(testVar);
        return new A();
    }

    @JCBean("BB-m")
    public B getB() {
        return new B(new A());
    }

//    @JCBean("AA-m1")
//    public A getA1(int q){
//        return new A();
//    }


}
