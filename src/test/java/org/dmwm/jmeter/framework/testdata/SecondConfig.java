package org.dmwm.jmeter.framework.testdata;

import com.google.common.collect.ImmutableMap;
import org.dmwm.jmeter.framework.JCBean;

import java.util.Map;

public class SecondConfig {

    @JCBean("DD-m")
    public D getD(){
        return new D();
    }

    @JCBean("M-map")
    public Map<String, Integer> getMap(){
        return ImmutableMap.<String, Integer>builder()
                .put("q", 1)
                .put("w", 2)
                .build();
    }

}
