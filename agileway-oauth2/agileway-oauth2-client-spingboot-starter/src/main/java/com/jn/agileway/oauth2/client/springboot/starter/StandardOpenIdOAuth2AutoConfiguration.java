package com.jn.agileway.oauth2.client.springboot.starter;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.declarative.DeclarativeHttpServiceProxyBuilder;
import com.jn.agileway.oauth2.client.OAuth2Properties;
import com.jn.agileway.oauth2.client.api.IntrospectEndpointAuthTokenSupplier;
import com.jn.agileway.oauth2.client.api.OAuth2ApiResponseConverter;
import com.jn.agileway.oauth2.client.api.OAuth2ApiService;
import com.jn.agileway.oauth2.client.api.std.BasicIntrospectEndpointAuthTokenSupplier;
import com.jn.agileway.oauth2.client.api.std.StandardOpenIdOAuth2Api;
import com.jn.agileway.oauth2.client.api.std.StandardOpenIdOAuth2ApiResponseConverter;
import com.jn.agileway.oauth2.client.api.std.StandardOpenIdOAuth2ApiService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(OAuth2ApiService.class)
@Configuration
public class StandardOpenIdOAuth2AutoConfiguration {

    @ConditionalOnMissingBean
    @Bean("httpExchanger")
    public HttpExchanger httpExchanger(OAuth2Properties oAuth2Properties) {
        HttpExchanger exchanger = new HttpExchanger();
        exchanger.init();
        return exchanger;
    }

    @Bean
    @ConditionalOnMissingBean
    public StandardOpenIdOAuth2ApiResponseConverter standardOpenIdOAuth2ApiResponsePayloadHandler() {
        return new StandardOpenIdOAuth2ApiResponseConverter();
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "agileway.oauth2.resourceserver.standard.introspect-endpoint-auth-type", havingValue = "Basic", matchIfMissing = true)
    public IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier(OAuth2Properties oAuth2Properties) {
        return new BasicIntrospectEndpointAuthTokenSupplier(oAuth2Properties);
    }

    @Bean
    public OAuth2ApiService oauth2ApiService(@Qualifier("httpExchanger")
                                             HttpExchanger httpExchanger,
                                             OAuth2Properties oAuth2Properties,
                                             IntrospectEndpointAuthTokenSupplier introspectEndpointAuthTokenSupplier,
                                             OAuth2ApiResponseConverter apiResponsePayloadHandler) {
        Class<? extends StandardOpenIdOAuth2Api> apiInterface = oAuth2Properties.getStandard().getApiInterface();
        if (apiInterface == null) {
            apiInterface = StandardOpenIdOAuth2Api.class;
        }
        Class theApiInterface = apiInterface;
        StandardOpenIdOAuth2Api api = (StandardOpenIdOAuth2Api) new DeclarativeHttpServiceProxyBuilder(theApiInterface)
                .withExchanger(httpExchanger)
                .build();
        StandardOpenIdOAuth2ApiService standardOAuth2ApiService = new StandardOpenIdOAuth2ApiService(api,
                oAuth2Properties,
                introspectEndpointAuthTokenSupplier,
                apiResponsePayloadHandler);
        return standardOAuth2ApiService;
    }

}
