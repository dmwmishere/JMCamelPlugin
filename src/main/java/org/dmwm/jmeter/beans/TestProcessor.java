package org.dmwm.jmeter.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.dmwm.jmeter.framework.JCBean;

import java.util.Map;

@SuppressWarnings("unchecked")
@JCBean("test-map-proc")
public class TestProcessor implements Processor {
    @Override
    public void process(Exchange exchange) {
        Map<String, Object> m = exchange.getIn().getBody(Map.class);
        m.put("response", exchange.getContext().getName() + "-" + exchange.getIn().getHeader("header1")
         + "-" + exchange.getIn().getHeader("header2"));
    }
}
