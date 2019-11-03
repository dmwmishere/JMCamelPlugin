package org.dmwm.jmeter.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.dmwm.jmeter.framework.JCBean;

import java.util.Map;

@JCBean("test-map-proc")
public class TestProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Map m = exchange.getIn().getBody(Map.class);
        m.put("response", exchange.getContext().getName() + "-" + exchange.getIn().getHeader("header1")
         + "-" + exchange.getIn().getHeader("header2"));
    }
}
