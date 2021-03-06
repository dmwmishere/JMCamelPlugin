package org.dmwm.jmeter.framework;

import com.google.common.collect.ImmutableMap;
import org.dmwm.jmeter.framework.testdata.*;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasEntry;

public class TestPicoRegistry {

    private final ImmutableMap<String, Class<?>> TEST_DATA =
            ImmutableMap.<String, Class<?>>builder()
                    .put("BB", B.class)
                    .put("CC", C.class)
                    .put("DD", D.class)
                    .put("EE", E.class)
                    .put("FF", F.class)
                    .build();
    private PicoRegistry registry;

    @Before
    public void init() {
        registry = new PicoRegistry();
        registry.addComponent(A.class);
        TEST_DATA.forEach((key, value) -> registry.addComponent(key, value));
    }

    @Test
    public void test_00_0_lookupByName() {

        System.out.println(registry.lookupByName("BB"));
        System.out.println(registry.lookupByName("TEST"));

        TEST_DATA.forEach(
                (alias, clazz) -> assertThat(registry.lookupByName(alias), instanceOf(clazz))
        );
        assertThat(((B) registry.lookupByName("BB")).getA().getValue(), equalTo("QWERTY"));
        assertThat(registry.lookupByName("TEST"), is(nullValue()));

    }

    @Test
    public void test_00_1_lookupByNameAndType() {

        System.out.println(registry.lookupByNameAndType("BB", B.class));
        System.out.println(registry.lookupByNameAndType("BB", C.class));
        System.out.println(registry.lookupByNameAndType("CC", C.class));
        System.out.println(registry.lookupByNameAndType("DD", D.class));
        System.out.println(registry.lookupByNameAndType("DD", I.class));
        System.out.println(registry.lookupByNameAndType("QQ", I.class));
        System.out.println(registry.lookupByNameAndType("QQ", A.class));

        // POSITIVE:
        TEST_DATA.forEach((alias, clazz) ->
                assertThat(registry.lookupByNameAndType(alias, clazz), instanceOf(clazz)));
        assertThat(registry.lookupByNameAndType("DD", I.class), instanceOf(D.class));
        assertThat(registry.lookupByNameAndType("BB", C.class), is(nullValue()));
        assertThat(registry.lookupByNameAndType("QQ", A.class), is(nullValue()));

    }

    @Test
    public void test_00_3_findByTypeWithName() {
        System.out.println(registry.findByTypeWithName(B.class).toString());
        TEST_DATA.forEach((alias, clazz) ->
                assertThat(registry.findByTypeWithName(clazz), hasEntry(
                        is(alias), instanceOf(clazz)))
        );

        System.out.println(registry.findByTypeWithName(I.class).toString());
        System.out.println(registry.findByTypeWithName(ImmutableMap.class));

        assertThat(registry.findByTypeWithName(I.class), allOf(
                hasEntry(is("CC"), instanceOf(C.class)),
                hasEntry(is("DD"), instanceOf(D.class))
                )
        );

        assertThat(registry.findByTypeWithName(ImmutableMap.class).size(), equalTo(0));

    }

    @Test
    public void test_00_4_findByType() {
        System.out.println("I = " + registry.findByType(I.class).toString());
        System.out.println("C = " + registry.findByType(C.class).toString());

        assertThat(registry.findByType(A.class), contains(instanceOf(A.class)));
        assertThat(registry.findByType(B.class), contains(instanceOf(B.class)));

        assertThat(registry.findByType(C.class), hasItems(
                instanceOf(C.class), instanceOf(D.class)));

        assertThat(registry.findByType(I.class), hasItems(
                instanceOf(C.class), instanceOf(D.class)));

        assertThat(registry.findByType(ImmutableMap.class).size(), equalTo(0));

    }

    @Test
    public void test_01_0_injection_types() {

        System.out.println(registry.lookupByType(F.class));
        System.out.println(registry.lookupByType(E.class));

        assertThat(registry.findByType(E.class), contains(instanceOf(E.class)));
        assertThat(registry.lookupByNameAndType("EE", E.class).getB(), is(notNullValue()));
        assertThat(registry.findByType(F.class), contains(instanceOf(F.class)));
        assertThat(registry.lookupByNameAndType("FF", F.class).getA(), is(notNullValue()));
        assertThat(registry.lookupByNameAndType("FF", F.class).getE(), is(notNullValue()));

    }

}
