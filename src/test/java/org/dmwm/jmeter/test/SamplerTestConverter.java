package org.dmwm.jmeter.test;

import org.dmwm.jmeter.framework.converter.Converter;

public class SamplerTestConverter  implements Converter<String> {

    @Override
    public String convert(String input) throws Exception {
        System.out.println("Test converter: " + input);
        return "test-converted-" + input;
    }
}
