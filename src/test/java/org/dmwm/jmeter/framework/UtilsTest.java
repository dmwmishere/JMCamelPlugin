package org.dmwm.jmeter.framework;

import org.apache.jmeter.threads.JMeterVariables;
import org.dmwm.jmeter.framework.testdata.D;
import org.dmwm.jmeter.util.CamelContextUtils;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UtilsTest {

    JMeterVariables vars = new JMeterVariables();

    @Before
    public void init(){
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata");
    }

    @Test
    public void test_00_0_registry_type_beans() {
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);

        assertThat(registry.lookup("AA"), notNullValue());
        assertThat(registry.lookup("DD"), notNullValue());
        assertThat(((D) registry.lookup("DD")).getVAL(), equalTo(666));
    }

    @Test
    public void test_00_1_registry_method_beans() {
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);

        assertThat(registry.lookup("AA-m"), notNullValue());
        assertThat((Map<String, Integer>)registry.lookupByNameAndType("M-map", Map.class)
        , hasEntry("q", 1));
    }

    @Test
    public void test_01_0_single_package() {
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);

        assertThat(registry.lookup("NN"), nullValue());
    }

    @Test
    public void test_01_2_multiple_packages() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata:org.dmwm.jmeter.framework.secondpackage");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);

        assertThat(registry.lookup("NN"), notNullValue());
        assertThat(registry.lookup("NN-m"), notNullValue());

    }

    @Test
    public void test_02_0_single_class() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata.A:org.dmwm.jmeter.framework.testdata.B");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);
        assertThat(registry.lookup("BB"), notNullValue());
        assertThat(registry.lookup("DD"), nullValue());
    }

    @Test
    public void test_02_1_invalid_config_constructor() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.invalid.InvalidConfigConstructor");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);
        assertThat(registry.lookup("Invalid-constructor"), nullValue());

    }

    @Test()
    public void test_02_2_invalid_config_method() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.invalid.InvalidConfigMethod");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);
        assertThat(registry.lookup("invalid-method"), nullValue());
    }

    @Test
    public void test_02_3_bean_exception() throws Exception {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.invalid.ExceptionInMethodCall");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);
        assertThat(registry.lookup("exception-bean"), nullValue());
    }

    @Test
    public void test_03_0_vars_injection() {
        vars.put("test-variable", "qwertyy");
        PicoRegistry registry = CamelContextUtils.initRegistry(vars);
        assertThat(registry.findByTypeWithName(JMeterVariables.class).get("jmvars").get("test-variable"), equalTo("qwertyy"));
        System.out.println("VARS = " + registry.findByTypeWithName(JMeterVariables.class).get("jmvars").get("test-variable"));
    }

}
