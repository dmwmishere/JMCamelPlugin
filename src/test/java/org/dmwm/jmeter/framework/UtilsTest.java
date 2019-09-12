package org.dmwm.jmeter.framework;

import org.dmwm.jmeter.framework.testdata.D;
import org.dmwm.jmeter.util.CamelContextUtils;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UtilsTest {

    @Before
    public void init(){
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata");
    }

    @Test
    public void test_00_0_registry_type_beans() {
        PicoRegistry registry = CamelContextUtils.initRegistry();

        assertThat(registry.lookup("AA"), notNullValue());
        assertThat(registry.lookup("DD"), notNullValue());
        assertThat(((D) registry.lookup("DD")).getVAL(), equalTo(666));
    }

    @Test
    public void test_00_1_registry_method_beans() {
        PicoRegistry registry = CamelContextUtils.initRegistry();

        assertThat(registry.lookup("AA-m"), notNullValue());
        assertThat((Map<String, Integer>)registry.lookupByNameAndType("M-map", Map.class)
        , hasEntry("q", 1));
    }

    @Test
    public void test_01_0_single_package() {
        PicoRegistry registry = CamelContextUtils.initRegistry();

        assertThat(registry.lookup("NN"), nullValue());
    }

    @Test
    public void test_01_2_multiple_packages() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata:org.dmwm.jmeter.framework.secondpackage");
        PicoRegistry registry = CamelContextUtils.initRegistry();

        assertThat(registry.lookup("NN"), notNullValue());
        assertThat(registry.lookup("NN-m"), notNullValue());

    }

    @Test
    public void test_02_0_single_class() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.testdata.A:org.dmwm.jmeter.framework.testdata.B");
        PicoRegistry registry = CamelContextUtils.initRegistry();
        assertThat(registry.lookup("BB"), notNullValue());
        assertThat(registry.lookup("DD"), nullValue());
    }

    @Test(expected = RuntimeException.class)
    public void test_02_1_invalid_config_constructor() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.invalid.InvalidConfigConstructor");
        CamelContextUtils.initRegistry();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_02_2_invalid_config_method() {
        System.setProperty("bean_class_path", "org.dmwm.jmeter.framework.invalid.InvalidConfigMethod");
        CamelContextUtils.initRegistry();
    }

}
