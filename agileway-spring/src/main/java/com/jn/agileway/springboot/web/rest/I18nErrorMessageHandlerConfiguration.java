package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.web.rest.I18nRestErrorMessageHandler;
import com.jn.agileway.web.rest.I18nRestErrorMessageHandlerProperties;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.i18n.AbstractResourceBundleI18nMessageStorage;
import com.jn.langx.text.i18n.HierarchicalI18nMessageStorage;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.text.i18n.JdkResourceBundleI18nMessageStorage;
import com.jn.langx.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${agileway.rest.global-exception-handler.i18n.bundle-basename:agileway_error}")
    private String defaultBundleBasename = "agileway_error";

    @Bean(name = "globalI18nErrorMessageStorage")
    @ConditionalOnMissingBean(name = "globalI18nErrorMessageStorage")
    public I18nMessageStorage i18nMessageStorage() {
        JdkResourceBundleI18nMessageStorage storage = new JdkResourceBundleI18nMessageStorage();
        storage.setBasename(Strings.useValueIfBlank(defaultBundleBasename, "agileway_error"));
        return storage;
    }

    private I18nMessageStorage getRootI18nMessageStorage(@NonNull I18nMessageStorage storage) {
        if (storage instanceof HierarchicalI18nMessageStorage) {
            HierarchicalI18nMessageStorage s = (HierarchicalI18nMessageStorage) storage;
            if (s.getParent() == null) {
                return storage;
            }
            return getRootI18nMessageStorage(s.getParent());
        } else {
            return storage;
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public I18nRestErrorMessageHandler i18nRestErrorMessageHandler(@Qualifier("globalI18nErrorMessageStorage") I18nMessageStorage storage, I18nRestErrorMessageHandlerProperties i18nRestErrorMessageHandlerProperties) {
        I18nRestErrorMessageHandler i18nRestErrorMessageHandler = new I18nRestErrorMessageHandler();

        I18nMessageStorage root = getRootI18nMessageStorage(storage);
        if (root != null) {
            if (root instanceof AbstractResourceBundleI18nMessageStorage) {
                String bundle = ((AbstractResourceBundleI18nMessageStorage) root).getBaseName();
                if (!Strings.equalsIgnoreCase("agileway_error", bundle)) {
                    JdkResourceBundleI18nMessageStorage agileway = new JdkResourceBundleI18nMessageStorage();
                    agileway.setBasename("agileway_error");
                    ((HierarchicalI18nMessageStorage) root).setParent(agileway);
                }
            } else if (root instanceof HierarchicalI18nMessageStorage) {
                JdkResourceBundleI18nMessageStorage agileway = new JdkResourceBundleI18nMessageStorage();
                agileway.setBasename("agileway_error");
                ((HierarchicalI18nMessageStorage) root).setParent(agileway);
            }
        }
        i18nRestErrorMessageHandler.setI18nMessageStorage(storage);
        i18nRestErrorMessageHandler.setConfig(i18nRestErrorMessageHandlerProperties);
        return i18nRestErrorMessageHandler;
    }
}
