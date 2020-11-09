package com.jn.agileway.springboot.httpclient;

import com.jn.agileway.httpclient.HttpClientCustomizer;
import com.jn.agileway.httpclient.HttpClientProperties;
import com.jn.agileway.httpclient.HttpClientProvider;
import com.jn.langx.util.collection.Pipeline;
import org.apache.http.client.HttpClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@ConditionalOnClass(HttpClient.class)
@ConditionalOnProperty(name = "agileway.httpclient.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnMissingBean(HttpClientAutoConfiguration.class)
@Configuration
public class HttpClientAutoConfiguration {

    @ConditionalOnMissingBean(name = "agilewayHttpClientProperties")
    @Bean(name = "agilewayHttpClientProperties")
    @ConfigurationProperties(prefix = "agileway.httpclient")
    public HttpClientProperties httpClientProperties() {
        return new HttpClientProperties();
    }

    @Bean(name = "agilewayHttpClient")
    @ConditionalOnMissingBean(name = "agilewayHttpClient")
    public HttpClient httpClient(HttpClientProvider httpClientProvider) {
        return httpClientProvider.get();
    }

    @Bean(name = "agilewayHttpClientProvider")
    @ConditionalOnMissingBean(name = "agilewayHttpClientProvider")
    public HttpClientProvider httpClientProvider(
            @Qualifier("agilewayHttpClientProperties") HttpClientProperties httpClientProperties,
            ObjectProvider<List<HttpClientCustomizer>> httpClientCustomizersProviders) {
        HttpClientProvider provider = new HttpClientProvider();
        provider.setConfig(httpClientProperties);
        provider.setCustomizers(httpClientCustomizersProviders.getObject());
        provider.startup();
        return provider;
    }
}
