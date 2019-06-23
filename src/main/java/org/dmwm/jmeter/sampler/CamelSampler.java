package org.dmwm.jmeter.sampler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.dmwm.jmeter.config.CamelConfigElement;

import java.util.Optional;

@Slf4j
@Getter
@Setter
public class CamelSampler extends AbstractTestElement implements Sampler, TestBean {

    private String contextName;
    private String directName;
    private String body;

    @Override
    public SampleResult sample(Entry entry) {
        SampleResult res = new SampleResult();
        log.debug("Send to {}:{} body: {}", contextName, directName, body);
        res.setSampleLabel(contextName + "-" + directName);
        res.setSuccessful(true);
        res.setSamplerData(body);
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");
        res.setResponseMessageOK();
        res.setResponseCodeOK();
        res.sampleStart();
        try {
            CamelContext cctx = CamelConfigElement.getContext(contextName);

            FluentProducerTemplate producer = cctx.createFluentProducerTemplate().to(directName);

            Exchange exchange = producer.withBody(body).send();

            res.setRequestHeaders(exchange.getOut().getHeaders().toString());
            res.setResponseData(Optional.ofNullable(exchange.getIn().getBody())
                    .orElse("null").toString());
            res.setResponseHeaders(exchange.getIn().getHeaders().toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.setResponseData(Optional.ofNullable(e.getMessage()).orElse("NO MESSAGE").getBytes());
            res.setSuccessful(false);
        }
        res.sampleEnd();
        return res;
    }
}
