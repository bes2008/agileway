package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.web.rest.I18nRestErrorMessageHandler;
import com.jn.agileway.web.rest.I18nRestErrorMessageHandlerProperties;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.text.i18n.JdkResourceBundleI18nMessageStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class I18nErrorMessageHandlerConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "agileway.rest.global-exception-handler.i18n")
    public I18nRestErrorMessageHandlerProperties i18nRestErrorMessageHandlerProperties() {
        return new I18nRestErrorMessageHandlerProperties();
    }

    @Bean(name = "globalI18nErrorMessageStorage")
    @ConditionalOnMissingBean(name = "globalI18nErrorMessageStorage")
    public I18nMessageStorage i18nMessageStorage() {
        JdkResourceBundleI18nMessageStorage storage = new JdkResourceBundleI18nMessageStorage();
        storage.setBasename("agileway_error");
        return storage;
    }

    @Bean
    @ConditionalOnMissingBean
    public I18nRestErrorMessageHandler i18nRestErrorMessageHandler(@Qualifier("globalI18nErrorMessageStorage") I18nMessageStorage storage, I18nRestErrorMessageHandlerProperties i18nRestErrorMessageHandlerProperties) {
        I18nRestErrorMessageHandler i18nRestErrorMessageHandler = new I18nRestErrorMessageHandler();
        i18nRestErrorMessageHandler.setI18MessageStorage(storage);
        i18nRestErrorMessageHandler.setConfig(i18nRestErrorMessageHandlerProperties);
        return i18nRestErrorMessageHandler;
    }
}
