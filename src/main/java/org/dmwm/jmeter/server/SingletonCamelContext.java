package org.dmwm.jmeter.server;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class SingletonCamelContext {

    private static CamelContext cctx;

    private SingletonCamelContext(){}

    public static CamelContext instance() throws Exception {
        if(cctx == null){
            synchronized (SingletonCamelContext.class) {
                if(cctx == null) {
                    DefaultCamelContext context = new DefaultCamelContext();

                    context.addRoutes(new RouteBuilder() {
                        @Override
                        public void configure() throws Exception {
                            from("direct:test")
                                    .log("qwerty ${in.body}");
                        }
                    });
                    cctx = context;
                    cctx.start();
                }
            }
        }
        return cctx;
    }


}
