package org.dmwm.jmeter.data;

import org.apache.jmeter.testelement.AbstractTestElement;

public class ExchangeSettingPair extends AbstractTestElement {

    private static final long serialVersionUID = 1965376937881090519L;

    public static final String EXCHANGE_SETTING_NAME = "exchangeSettingName";

    public static final String EXCHANGE_SETTING_VALUE = "exchangeSettingValue";

    public void setExchangeSettingName(String name){
        setProperty(EXCHANGE_SETTING_NAME, name);
    }

    public void setExchangeSettingValue(String value){
        setProperty(EXCHANGE_SETTING_VALUE, value);
    }

    public String getExchangeSettingName(){
        return getProperty(EXCHANGE_SETTING_NAME).getStringValue();
    }

    public String getExchangeSettingValue(){
        return getProperty(EXCHANGE_SETTING_VALUE).getStringValue();
    }

}
