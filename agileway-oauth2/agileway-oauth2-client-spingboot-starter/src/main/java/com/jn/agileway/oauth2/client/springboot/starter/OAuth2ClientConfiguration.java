package com.jn.agileway.oauth2.client.springboot.starter;

import com.jn.agileway.oauth2.client.*;
import com.jn.agileway.oauth2.client.api.OAuth2ApiService;
import com.jn.agileway.oauth2.client.userinfo.*;
import com.jn.agileway.oauth2.client.validator.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class OAuth2ClientConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "agileway.oauth2.client")
    public OAuth2ClientProperties oauth2Properties() {
        return new OAuth2ClientProperties();
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
            OAuth2ClientProperties oAuth2Properties,
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
    public FilterRegistrationBean<OAuth2AuthzFilter> oAuth2AuthzFilterRegistrationBean(
            OAuth2AuthzHandler oAuth2AuthzHandler, OAuth2ClientProperties oAuth2ClientProperties) {
        OAuth2ClientProperties.FilterConfig filterConfig = oAuth2ClientProperties.getFilter();

        OAuth2AuthzFilter filter = new OAuth2AuthzFilter(oAuth2ClientProperties, oAuth2AuthzHandler);
        FilterRegistrationBean<OAuth2AuthzFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(filterConfig.getOrder());

        Set<String> urlPatterns = new HashSet<>(filterConfig.getUrlPatterns());
        urlPatterns.add(oAuth2ClientProperties.getCallbackUri());

        registrationBean.addUrlPatterns(urlPatterns.toArray(new String[0]));
        registrationBean.setName(filterConfig.getName());
        registrationBean.setEnabled(filterConfig.isEnabled());
        return registrationBean;
    }
}
