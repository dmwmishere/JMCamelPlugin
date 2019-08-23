package org.dmwm.jmeter.config;

import com.helger.commons.io.stream.StringInputStream;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.CamelContext;
import org.apache.camel.CamelException;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.jmeter.config.ConfigElement;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jmeter.testbeans.TestBeanHelper;
import org.apache.jmeter.testelement.AbstractTestElement;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.data.RegistryTableElement;
import org.dmwm.jmeter.framework.JCBean;
import org.dmwm.jmeter.framework.PicoRegistry;
import org.dmwm.jmeter.util.CamelContextUtils;
import org.reflections.Reflections;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Set;

@Slf4j
@Getter
@Setter
public class CamelConfigElement extends AbstractTestElement
        implements ConfigElement, TestBean, TestStateListener {

    private final static String[] CLASS_PATHS = System.getProperty("bean_class_path", "org.dmwm.jmeter.beans").split(":");

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

        PicoRegistry registry = new PicoRegistry();

        registryBeans.forEach(element ->
                CamelContextUtils.initBean(element.getName(), element.getClazz(), registry));
        Set<Class<?>> classes = new Reflections(CLASS_PATHS).getTypesAnnotatedWith(JCBean.class);
        log.info("Found classes to add to camel context: {}", classes);
        classes.forEach(clazz -> registry.addComponent(clazz.getAnnotation(JCBean.class).value(), clazz));
        log.info("CCBG TEST STARTED");
        JMeterVariables variables = getThreadContext().getVariables();
        if (variables.getObject(contextName) == null) {
            synchronized (this) {
                cctx = new DefaultCamelContext(registry);
                ((DefaultCamelContext) cctx).setName(contextName);
                try {
                    RoutesDefinition routes =
                            cctx.loadRoutesDefinition(new StringInputStream(routeXml, Charset.defaultCharset()));
                    cctx.addRouteDefinitions(routes.getRoutes());
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
