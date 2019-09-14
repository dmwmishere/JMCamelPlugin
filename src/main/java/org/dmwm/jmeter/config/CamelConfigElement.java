package org.dmwm.jmeter.config;

import com.helger.commons.io.stream.StringInputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelException;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.data.RegistryTableElement;
import org.dmwm.jmeter.framework.ContextBuilder;
import org.dmwm.jmeter.framework.PicoRegistry;
import org.dmwm.jmeter.util.CamelContextUtils;

import java.nio.charset.Charset;
import java.util.Collection;

@Slf4j
@Getter
@Setter
public class CamelConfigElement extends AbstractTestElement
        implements ConfigElement, TestBean, TestStateListener {

    private static final long serialVersionUID = 1065500169997779419L;

    private String contextName;
    private String routeDefFile;
    private String routeXml;
    private Collection<RegistryTableElement> registryBeans;

    private transient CamelContext cctx;

    public static CamelContext getContext(String contextName) throws CamelException {
        Object cctxObj =
                JMeterContextService.getContext().getVariables().getObject(contextName);

        if (cctxObj == null) {
            throw new CamelException("Context " + contextName + " not defined!");
        } else {
            if (cctxObj instanceof CamelContext) {
                return (CamelContext) cctxObj;
            } else {
                throw new CamelException("Object " + contextName +
                        " not a camel context: " + cctxObj.getClass().getName());
            }
        }

    }

    @Override
    public void addConfigElement(ConfigElement config) {
        System.out.println("CCBG addConfigElement");
    }

    @Override
    public boolean expectsModification() {
        return false;
    }

    @Override
    public void testStarted() {
        this.setRunningVersion(true);
        TestBeanHelper.prepare(this);

        PicoRegistry registry = CamelContextUtils.initRegistry();

        registryBeans.forEach(element ->
                CamelContextUtils.initBean(element.getName(), element.getClazz(), registry));

        log.info("CCBG TEST STARTED");
        JMeterVariables variables = getThreadContext().getVariables();
        if (variables.getObject(contextName) == null) {
            synchronized (this) {
                try {
                    cctx = ContextBuilder
                            .builder()
                            .setName(contextName)
                            .setRegistry(registry)
                            .setProperties(CamelContextUtils.initProperties(getThreadContext()))
                            .addRoutes(new StringInputStream(routeXml, Charset.defaultCharset()))
                            .build();
                    cctx.start();
                    variables.putObject(contextName, cctx);
                } catch (Exception e) {
                    log.error("Failed to create camel configuration for " + this.getName() +
                            ": " + e.getClass() + " - " + e.getMessage());
                }
            }
        } else {
            log.error("Camel context with name {} already defined!", contextName);
        }

    }

    @Override
    public void testStarted(String host) {
        testStarted();
    }

    @Override
    public void testEnded() {
        log.info("CCBG TEST ENDED");
        try {
            cctx.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testEnded(String host) {
        testEnded();
    }

}
