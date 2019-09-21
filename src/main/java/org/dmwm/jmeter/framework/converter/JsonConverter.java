package org.dmwm.jmeter.framework.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter implements Converter<JsonNode> {

    private ObjectMapper om = new ObjectMapper();

    @Override
    public JsonNode convert(String input) throws Exception {
        return om.readTree(input);
    }
}
