package org.dmwm.jmeter.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.Registry;
import org.dmwm.jmeter.util.CamelContextUtils;

import java.io.InputStream;
import java.util.Properties;

/**
 * implements builder for default camel context
 *
 * @see DefaultCamelContext
 */
public class ContextBuilder implements Builder {

    private final DefaultCamelContext cctx;

    public ContextBuilder() {
        this.cctx = new DefaultCamelContext();
    }

    @Override
    public Builder addRoutes(InputStream routesXmlContent) throws ContextConfigurationException {
        try {
            addRoutes(cctx.loadRoutesDefinition(routesXmlContent));
        } catch (Exception e) {
            throw new ContextConfigurationException(e);
        }
        return this;
    }

    @Override
    public Builder addRoutes(RouteBuilder routeBuilder) throws ContextConfigurationException {
        try {
            cctx.addRoutes(routeBuilder);
        } catch (Exception e) {
            throw new ContextConfigurationException(e);
        }
        return this;
    }

    @Override
    public Builder addRoutes(RoutesDefinition routesDefinition) throws ContextConfigurationException {
        try {
            cctx.addRouteDefinitions(routesDefinition.getRoutes());
        } catch (Exception e) {
            throw new ContextConfigurationException(e);
        }
        return this;
    }

    @Override
    public Builder setName(String contextName) {
        cctx.setName(contextName);
        return this;
    }

    @Override
    public Builder setProperties(PropertiesComponent propertiesComponent) {
        cctx.addComponent("properties", propertiesComponent);
        return this;
    }

    @Override
    public Builder setProperties(Properties properties) {
        setProperties(CamelContextUtils.initProperties(properties));
        return this;
    }

    @Override
    public Builder setRegistry(Registry registry) {
        cctx.setRegistry(registry);
        return this;
    }

    @Override
    public CamelContext build() {
        return cctx;
    }

    public static Builder builder() {
        return new ContextBuilder();
    }

}
