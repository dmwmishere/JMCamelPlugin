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
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.config.CamelConfigElement;
import org.dmwm.jmeter.data.ExchangeSettingPair;
import org.dmwm.jmeter.framework.converter.Converter;
import org.dmwm.jmeter.util.CamelContextUtils;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Objects;

@Slf4j
@Getter
@Setter
public class CamelSampler extends AbstractSampler implements TestBean, ThreadListener {

    private static final long serialVersionUID = 277235054922307347L;

    static final String SAVE_AS_STRING = "STRING";
    static final String SAVE_AS_OBJECT = "OBJECT";
    static final String SAVE_EXCHANGE = "EXCHANGE";

    private String camelContextName;
    private String directName;
    private String body;
    private String converterClass;
    private String saveResultAs;
    private String resultName;

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
                exchangeHeaders.forEach(header -> producer.withHeader(
                        header.getExchangeSettingName(),
                        header.getExchangeSettingValue()
                ));
            }

            Exchange exchange = producer
                    .withBody(Objects.isNull(converter) ? body : converter.convert(body))
                    .send();

            res.setDataType(SampleResult.TEXT);
            res.setContentType("text/plain");
            res.setResponseData(saveResults(exchange));
            res.setRequestHeaders(exchange.getOut().getHeaders().toString());
            res.setResponseHeaders(exchange.getIn().getHeaders().toString());
            if (exchange.getException() != null) {
                Exception exception = exchange.getException();
                res.setResponseCode("500");
                res.setResponseMessage(exception.getClass().getSimpleName() + ":" + exception.getMessage());
                res.setSuccessful(false);
            }
        } catch (Exception e) {
            res.setDataType(SampleResult.TEXT);
            res.setResponseData((e.getClass().getSimpleName() + ":" + e.getMessage()).getBytes());
            res.setResponseCode("500");
            res.setSuccessful(false);
        }
        res.sampleEnd();
        return res;
    }

    private byte[] saveResults(Exchange exchange) {

        Object body = exchange.getIn().getBody();

        if (!resultName.isEmpty()) {
            JMeterVariables vars = getThreadContext().getVariables();
            if (saveResultAs.equals(SAVE_AS_STRING)) {
                vars.putObject(resultName, body.toString());
            } else if (saveResultAs.equals(SAVE_AS_OBJECT)) {
                vars.putObject(resultName, body);
            } else if (saveResultAs.equals(SAVE_EXCHANGE)) {
                vars.putObject(resultName, exchange);
            }
        }
        return body.toString().getBytes(Charset.defaultCharset());
    }

    @Override
    public void threadStarted() {
        firstSample = true;
    }

    @Override
    public void threadFinished() {
    }
}
