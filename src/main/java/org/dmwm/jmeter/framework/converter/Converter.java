package org.dmwm.jmeter.framework.converter;

public interface Converter<T> {
    T convert(String input) throws Exception;
}
