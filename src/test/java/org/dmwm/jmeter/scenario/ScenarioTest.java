package org.dmwm.jmeter.scenario;

import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.dmwm.jmeter.test.AbstractJMeterTest;
import org.dmwm.jmeter.test.TestListener;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ScenarioTest extends AbstractJMeterTest {

    public ScenarioTest() throws IOException {
        testPlanTree = SaveService.loadTree(new File("scenario/TestPlugin.jmx"));
    }

    @Before
    public void init() {
        plan = (TestPlan) testPlanTree.getArray()[0];
        listener = new TestListener(plan, "jm-camel-context-1");
        StandardJMeterEngine.register(listener);
    }

    @Test
    public void test_00_0_complex_run() {

        listener.addPreCheck((cctx, jmc) -> assertThat("Check context started",
                cctx.getStatus().isStarted(), equalTo(true)));

        listener.addPostCheck((cctx, jmc) -> assertThat(cctx.getStatus().isStopped(),
                equalTo(true)));

        configure(testPlanTree);
        run();

    }

}
