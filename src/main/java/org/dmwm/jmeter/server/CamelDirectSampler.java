package org.dmwm.jmeter.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.util.Optional;

@Slf4j
public class CamelDirectSampler extends AbstractJavaSamplerClient {

    private final static String DIRECT_NAME = "directName";
    private final static String BODY_CONTENT = "bodyContent";

    private CamelContext cctx;

    @Override
    public void setupTest(JavaSamplerContext context) {
        super.setupTest(context);
        try {
            cctx = SingletonCamelContext.instance();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
    }

    @Override
    public SampleResult runTest(JavaSamplerContext context) {
        String directName = context.getParameter(DIRECT_NAME);
        String body = context.getParameter(BODY_CONTENT);

        SampleResult res = new SampleResult();
        res.setSampleLabel(cctx.getName() + "-" + directName);
        res.setSuccessful(true);
        res.setSamplerData(body);
        res.setDataType(SampleResult.TEXT);
        res.setContentType("text/plain");
        res.setResponseMessageOK();
        res.setResponseCodeOK();
        res.sampleStart();

        Exchange exchange = cctx.createFluentProducerTemplate()
                .to("direct:".concat(directName))
                .withBody(body)
                .send();

        res.setRequestHeaders(exchange.getOut().getHeaders().toString());
        res.setResponseData(Optional.ofNullable(exchange.getIn().getBody())
                .orElse("null").toString());
        res.setResponseHeaders(exchange.getIn().getHeaders().toString());


//        Object response = cctx.createFluentProducerTemplate()
//                .to("direct:".concat(directName))
//                .withBody(body)
//                .request();
//
//        res.setResponseData(Optional.ofNullable(response)
//                .orElse("null").toString());

        res.sampleEnd();

        return res;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments params = new Arguments();
        params.addArgument(DIRECT_NAME, String.valueOf("test"));
        params.addArgument(BODY_CONTENT, String.valueOf("Sample Text"));
        return params;
    }
}
