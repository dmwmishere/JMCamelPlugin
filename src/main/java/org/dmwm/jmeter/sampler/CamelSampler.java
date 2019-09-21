package org.dmwm.jmeter.sampler;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.FluentProducerTemplate;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.ThreadListener;
import org.dmwm.jmeter.config.CamelConfigElement;
import org.dmwm.jmeter.framework.converter.Converter;
import org.dmwm.jmeter.util.CamelContextUtils;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Getter
@Setter
public class CamelSampler extends AbstractSampler implements TestBean, ThreadListener {

    private static final long serialVersionUID = 277235054922307347L;

    private String contextName;
    private String directName;
    private String body;
    private String converterClass;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient Converter<?> converter;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient boolean firstSample;


    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private transient FluentProducerTemplate producer;

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
            if (firstSample) {
                converter = CamelContextUtils.initConverter(converterClass);
                producer = CamelConfigElement.getContext(contextName)
                        .createFluentProducerTemplate().to(directName);
                firstSample = false;
            }

            Exchange exchange = producer.withBody(Objects.isNull(converter) ? body : converter.convert(body)).send();

            res.setRequestHeaders(exchange.getOut().getHeaders().toString());
            res.setResponseData(Optional.ofNullable(exchange.getIn().getBody())
                    .orElse("null").toString());
            res.setResponseHeaders(exchange.getIn().getHeaders().toString());

        } catch (Exception e) {
            e.printStackTrace();
            res.setResponseData(Optional.ofNullable(e.getMessage()).orElse("no message due to exception: " + e.getMessage()).getBytes());
            res.setSuccessful(false);
        }
        res.sampleEnd();
        return res;
    }

    @Override
    public void threadStarted() {
        firstSample = true;
    }

    @Override
    public void threadFinished() {
    }
}
