package org.dmwm.jmeter.test;

import org.apache.camel.CamelContext;
import org.apache.camel.StartupListener;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CamelContextStartedAssertListener implements StartupListener {

    private boolean contextStarted = false;

    private CamelContext context = null;

    @Override
    public void onCamelContextStarted(CamelContext context, boolean alreadyStarted) throws Exception {
        this.context = context;
        assertThat(alreadyStarted, equalTo(false));
        contextStarted = true;
    }

    public CamelContext startContext(CamelContext camelContext) {
        try {
            camelContext.addStartupListener(this);
            camelContext.start();
        } catch(Exception e){
            fail(e.getClass().getSimpleName() + " while starting context: " + e.getMessage());
        }
        return camelContext;

    }

    public void assertContextStarted(){
        assertThat(context, notNullValue());
        assertThat(contextStarted, equalTo(true));
    }

}
