package org.dmwm.jmeter.test;

import org.apache.camel.CamelContext;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.threads.JMeterContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import static org.junit.Assert.fail;

public class TestListener implements TestStateListener {

    private final String camelContextName;
    private final TestPlan plan;
    private CamelContext cctx;
    private List<BiConsumer<CamelContext, JMeterContext>> preChecks = new ArrayList<>();
    private List<BiConsumer<CamelContext, JMeterContext>> postChecks = new ArrayList<>();

    public TestListener(TestPlan plan, String camelContextName) {
        this.camelContextName = camelContextName;
        this.plan = plan;
    }

    public void addPreCheck(BiConsumer<CamelContext, JMeterContext> check) {
        preChecks.add(check);
    }

    public void addPostCheck(BiConsumer<CamelContext, JMeterContext> check) {
        postChecks.add(check);
    }

    @Override
    public void testStarted() {
        System.out.println("TEST LISTENER: STARTED. EXECUTE PRE CHECKS...");
        cctx = (CamelContext) plan.getThreadContext().getVariables().getObject(camelContextName);
        try {
            preChecks.forEach(check -> check.accept(cctx, plan.getThreadContext()));
        } catch (Throwable t) {
            fail(t.getClass().getSimpleName() + " while performing pre check: " + t.getMessage());
        }
    }

    @Override
    public void testStarted(String host) {
        testStarted();
    }

    @Override
    public void testEnded() {
        System.out.println("TEST LISTENER: ENDED. EXECUTE POST CHECKS...");
        try {
            postChecks.forEach(check -> check.accept(cctx, plan.getThreadContext()));
        } catch (Throwable t) {
            fail(t.getClass().getSimpleName() + " while performing post check: " + t.getMessage());
        }
    }

    @Override
    public void testEnded(String host) {
        testEnded();
    }
}