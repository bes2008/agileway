package com.jn.agileway.oauth2.client.springboot.starter;

import com.jn.agileway.oauth2.client.*;
import com.jn.agileway.oauth2.client.api.OAuth2ApiService;
import com.jn.agileway.oauth2.client.userinfo.*;
import com.jn.agileway.oauth2.client.validator.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth2ClientConfiguration {
    private OAuth2Properties oAuth2Properties;

    @Autowired
    @ConfigurationProperties(prefix = "agileway.oauth2")
    public void setoAuth2Properties(OAuth2Properties oAuth2Properties) {
        this.oAuth2Properties = oAuth2Properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2StateValidator oAuth2StateValidator() {
        return new DefaultStateValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2TokenValidator oAuth2TokenValidator() {
        return new OAuth2TokenValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public BearerAccessTokenValidator bearerAccessTokenValidator() {
        return new BearerAccessTokenValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public IntrospectAccessTokenValidator introspectAccessTokenValidator() {
        return new IntrospectAccessTokenValidator();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenIdTokenParser openIdTokenParser() {
        return new JwsOpenIdTokenParser();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenIdTokenUserinfoExtractor openIdTokenUserInfoExtractor() {
        return new DefaultOpenIdTokenUserInfoExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    public IntrospectResultUserInfoExtractor introspectResultUserInfoExtractor() {
        return new DefaultIntrospectResultUserInfoExtractor();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2CallbackUriSupplier oauth2CallbackUriSupplier() {
        return new DefaultOAuth2CallbackUriSupplier();
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2AuthzHandler oAuth2AuthzHandler(
            OAuth2ApiService oAuth2ApiService,
            OAuth2StateValidator oAuth2StateValidator,
            OAuth2TokenValidator oAuth2TokenValidator,
            BearerAccessTokenValidator bearerAccessTokenValidator,
            IntrospectAccessTokenValidator introspectAccessTokenValidator,
            OpenIdTokenParser openIdTokenParser,
            OpenIdTokenUserinfoExtractor openIdTokenUserInfoExtractor,
            IntrospectResultUserInfoExtractor introspectResultUserInfoExtractor,
            OAuth2CallbackUriSupplier oauth2CallbackUriSupplier) {
        return new OAuth2AuthzHandler(
                oAuth2Properties,
                oAuth2ApiService,
                oAuth2StateValidator,
                oAuth2TokenValidator,
                bearerAccessTokenValidator,
                introspectAccessTokenValidator,
                openIdTokenParser,
                openIdTokenUserInfoExtractor,
                introspectResultUserInfoExtractor,
                oauth2CallbackUriSupplier);
    }

    @Bean
    public FilterRegistrationBean<OAuth2AuthzFilter> oAuth2AuthzFilterRegistrationBean(OAuth2AuthzHandler oAuth2AuthzHandler) {
        OAuth2AuthzFilter filter = new OAuth2AuthzFilter(oAuth2Properties, oAuth2AuthzHandler);
        FilterRegistrationBean<OAuth2AuthzFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(10);
        registrationBean.addUrlPatterns(
                oAuth2Properties.getCallbackUri(),
                "/auth/oauth2/login_to_um"
        );
        registrationBean.setName("OAuth2AuthzFilter");
        registrationBean.setEnabled(true);
        return registrationBean;
    }
}
