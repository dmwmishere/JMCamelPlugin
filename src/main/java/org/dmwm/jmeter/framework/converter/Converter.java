package org.dmwm.jmeter.framework.converter;

/**
 * converter from string into specified type
 * @param <T> object type to convert to
 */
public interface Converter<T> {

    /**
     * convert jmeter sample body into specified type
     * @param input jmeter sample body
     * @return object instance
     * @throws Exception if there was exception while conversion
     */
    T convert(String input) throws Exception;
}
