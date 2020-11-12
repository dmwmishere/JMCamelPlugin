package org.dmwm.jmeter.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.Registry;

import java.io.InputStream;
import java.util.Properties;

/**
 * Builds camel context with specified parameters
 * @see ContextBuilder
 */
public interface Builder {

    /**
     * add routes from input stream
     * @param routesXmlContent route <a href="https://camel.apache.org/manual/latest/xml-configuration.html">xml content</a>
     * @return this
     * @throws ContextConfigurationException wrapper for loadRoutesDefinition exception
     */
    Builder addRoutes(InputStream routesXmlContent) throws ContextConfigurationException;

    /**
     * add routes from RouteBuilder
     * @param routeBuilder route builder
     * @return this
     * @throws ContextConfigurationException wrapper for loadRoutesDefinition exception
     */
    Builder addRoutes(RouteBuilder routeBuilder) throws ContextConfigurationException;

    Builder addRoutes(RoutesDefinition routesDefinition) throws ContextConfigurationException;

    Builder setName(String contextName);

    Builder setProperties(PropertiesComponent propertiesComponent);

    Builder setProperties(Properties properties);

    Builder setRegistry(Registry registry);

    CamelContext build();

}
