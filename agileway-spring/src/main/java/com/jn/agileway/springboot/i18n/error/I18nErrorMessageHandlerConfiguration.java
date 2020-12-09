package com.jn.agileway.springboot.i18n.error;

import com.jn.agileway.spring.messagestorage.SpringMessageStorage;
import com.jn.agileway.springboot.i18n.SpringBootBuiltinMessageSourceProperties;
import com.jn.agileway.web.rest.I18nRestErrorMessageHandler;
import com.jn.agileway.web.rest.I18nRestErrorMessageHandlerProperties;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.text.i18n.AbstractResourceBundleI18nMessageStorage;
import com.jn.langx.text.i18n.HierarchicalI18nMessageStorage;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.text.i18n.JdkResourceBundleI18nMessageStorage;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import java.util.Set;

@Configuration
public class I18nErrorMessageHandlerConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "agileway.i18n.error")
    public I18nRestErrorMessageHandlerProperties i18nRestErrorMessageHandlerProperties() {
        return new I18nRestErrorMessageHandlerProperties();
    }

    @Bean(name = "springBootBuiltinMessageSourceProperties")
    @ConditionalOnMissingBean(name = "springBootBuiltinMessageSourceProperties")
    public SpringBootBuiltinMessageSourceProperties springBootBuiltinMessageSourceProperties() {
        return new SpringBootBuiltinMessageSourceProperties();
    }

    @Bean(name = "globalI18nErrorMessageStorage")
    @ConditionalOnMissingBean(name = "globalI18nErrorMessageStorage")
    public I18nMessageStorage globalI18nErrorMessageStorage(SpringBootBuiltinMessageSourceProperties properties) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        final Set<String> basenames = StringUtils.commaDelimitedListToSet(StringUtils.trimAllWhitespace(properties.getBasename()));
        basenames.add("agileway_error");

        String[] errorBasenames = Pipeline.of(basenames).filter(new Predicate<String>() {
            @Override
            public boolean test(String value) {
                return Strings.indexOf(value, "error", true) >= 0;
            }
        }).toArray(String[].class);
        messageSource.setBasenames(errorBasenames);

        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        messageSource.setCacheSeconds(properties.getCacheSeconds());
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());

        SpringMessageStorage errorMessageStorage = new SpringMessageStorage();
        errorMessageStorage.setMessageSource(messageSource);

        return errorMessageStorage;
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

    @Bean(name = "globalRestErrorMessageHandler")
    @ConditionalOnMissingBean(name = "globalRestErrorMessageHandler")
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
