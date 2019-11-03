package org.dmwm.jmeter.framework.converter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class JsonConverter implements Converter<Map> {

    private ObjectMapper om = new ObjectMapper();

    @Override
    public Map convert(String input) throws Exception {
        return om.readValue(input, Map.class);
    }
}
