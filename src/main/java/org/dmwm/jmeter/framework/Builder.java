package org.dmwm.jmeter.framework;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.Registry;

import java.io.InputStream;
import java.util.Properties;

public interface Builder {

    Builder addRoutes(InputStream routesXmlContent) throws Exception;

    Builder addRoutes(RouteBuilder routeBuilder) throws Exception;

    Builder addRoutes(RoutesDefinition routesDefinition) throws Exception;

    Builder setProperties(PropertiesComponent propertiesComponent);

    Builder setProperties(Properties properties);

    Builder setRegistry(Registry registry);

    CamelContext build();

}
