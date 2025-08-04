package com.jn.agileway.oauth2.authz.springboot.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OAuth2Properties.class)
public class OAuth2Configuration {
    private OAuth2Properties oAuth2Properties;

    @Autowired
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
