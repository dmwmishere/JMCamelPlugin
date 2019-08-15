package org.dmwm.jmeter.framework;

import java.lang.annotation.*;


@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JCBean {
    String value();
}
