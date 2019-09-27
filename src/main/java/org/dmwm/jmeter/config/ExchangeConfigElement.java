package org.dmwm.jmeter.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.testbeans.TestBean;
import org.dmwm.jmeter.data.ExchangeSettingPair;

import java.util.Collection;

@Getter
@Setter
public class ExchangeConfigElement extends ConfigTestElement implements TestBean {

    private static final long serialVersionUID = -9036623199066678642L;

    Collection<ExchangeSettingPair> exchangeProperties;
    Collection<ExchangeSettingPair> exchangeHeaders;


}
