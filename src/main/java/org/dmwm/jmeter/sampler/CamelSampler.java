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
import org.dmwm.jmeter.data.ExchangeSettingPair;
import org.dmwm.jmeter.framework.converter.Converter;
import org.dmwm.jmeter.util.CamelContextUtils;
import org.dmwm.jmeter.util.Serializer;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Getter
@Setter
public class CamelSampler extends AbstractSampler implements TestBean, ThreadListener {

    private static final long serialVersionUID = 277235054922307347L;

    private String camelContextName;
    private String directName;
    private String body;
    private String converterClass;
    private Boolean saveResultAs;

    private Collection<ExchangeSettingPair> exchangeHeaders;

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
        log.debug("Send to {}:{} body: {}", camelContextName, directName, body);
        res.setSampleLabel(getName() + "-" + camelContextName + "-" + directName);
        res.setSuccessful(true);
        res.setSamplerData(body);
        res.setResponseMessageOK();
        res.setResponseCodeOK();
        res.sampleStart();
        try {
            if (firstSample) {
                converter = CamelContextUtils.initConverter(converterClass);
                producer = CamelConfigElement.getContext(camelContextName)
                        .createFluentProducerTemplate().to(directName);
                firstSample = false;
            }

            if (exchangeHeaders != null) {
                exchangeHeaders.forEach(header -> {
                            producer.withHeader(
                                    header.getExchangeSettingName(),
                                    header.getExchangeSettingValue()
                            );
                        }
                );
            }

            Exchange exchange = producer
                    .withBody(Objects.isNull(converter) ? body : converter.convert(body))
                    .send();

            res.setRequestHeaders(exchange.getOut().getHeaders().toString());
            Object body = exchange.getIn().getBody();
            if (saveResultAs) {
                res.setDataType(SampleResult.TEXT);
                res.setContentType("text/plain");
                res.setResponseData(body.toString().getBytes(res.getDataEncodingWithDefault()));
            } else {
                res.setDataType(SampleResult.BINARY);
                res.setResponseData(Serializer.serialize(body));
            }

            res.setResponseHeaders(exchange.getIn().getHeaders().toString());

        } catch (Exception e) {
            res.setDataType(SampleResult.TEXT);
            res.setResponseData((e.getClass().getSimpleName() + ":" + e.getMessage()).getBytes());
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
