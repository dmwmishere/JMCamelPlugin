package org.dmwm.jmeter.framework.testdata;

public class C implements I {

    private final int VAL = 666;

    public C(){
        System.out.println("constructor C");
    }

    @Override
    public void action() {
        System.out.println("action C");
    }

    @Override
    public String toString() {
        return "C[ " + VAL + " ]";
    }
}
