package org.dmwm.jmeter.sampler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.dmwm.jmeter.config.CamelConfigElement;

@Slf4j
@Getter
@Setter
public class CamelSampler extends AbstractTestElement implements Sampler, TestBean {

    private String contextName;
    private String directName;
    private String body;



    @Override
    public SampleResult sample(Entry entry) {

        log.info("Send to {}:{} body: {}", contextName, directName, body);

        try {

            CamelContext cctx = CamelConfigElement.getContext(contextName);

            cctx.createFluentProducerTemplate().to(directName).withBody(body).send();

        } catch(Exception e){
            e.printStackTrace();
        }

        return new SampleResult();
    }
}
