package com.jn.agileway.springboot.web.filter;

import com.jn.agileway.spring.utils.SpringContextHolder;
import com.jn.agileway.web.filter.HttpRequestHandlerFilter;
import com.jn.agileway.web.filter.accesslog.AccessLogFilter;
import com.jn.agileway.web.filter.accesslog.WebAccessLogProperties;
import com.jn.agileway.web.filter.rr.RRFilter;
import com.jn.agileway.web.filter.waf.AllowedMethodsFilter;
import com.jn.agileway.web.filter.waf.SqlInjectionFilter;
import com.jn.agileway.web.filter.waf.XssFilter;
import com.jn.agileway.web.filter.waf.cors.CorsFilter;
import com.jn.agileway.web.filter.waf.cors.CorsProperties;
import com.jn.agileway.web.request.header.SetResponseHeaderHandler;
import com.jn.agileway.web.request.header.SetResponseHeaderProperties;
import com.jn.agileway.web.security.sqlinjection.SqlFirewall;
import com.jn.agileway.web.security.sqlinjection.SqlInjectionProperties;
import com.jn.agileway.web.security.sqlinjection.SqlInjectionWafFactory;
import com.jn.agileway.web.security.xss.XssFirewall;
import com.jn.agileway.web.security.xss.XssProperties;
import com.jn.agileway.web.security.xss.XssWafFactory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

@Configuration
public class AgilewayBasicFiltersConfiguration {

    @Value("${agileway.web.encoding:UTF-8}")
    private String encoding = "UTF-8";

    @Value("${agileway.web.streamWrapper:false}")
    private boolean streamWrapper = false;

    @Order(-103)
    @Bean
    public FilterRegistrationBean baseFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        RRFilter filter = new RRFilter();
        registration.setFilter(filter);
        Map<String, String> initialParameters = Collects.emptyHashMap();
        initialParameters.put("streamWrapperEnabled", "" + streamWrapper);
        initialParameters.put("encoding", encoding);
        registration.setInitParameters(initialParameters);
        registration.setUrlPatterns(Collects.newArrayList("/*"));
        registration.setOrder(-103);
        registration.setName("StreamWrapper Filter");
        return registration;
    }


    @ConfigurationProperties(prefix = "agileway.web.access-log")
    @Bean
    public WebAccessLogProperties accessLogProperties() {
        return new WebAccessLogProperties();
    }

    @Order(-102)
    @Bean
    @Autowired
    public FilterRegistrationBean accessLogFilterRegistrationBean(WebAccessLogProperties accessLogProperties) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        AccessLogFilter filter = new AccessLogFilter();
        filter.setConfig(accessLogProperties);
        registration.setFilter(filter);
        registration.setName("AccessLog Filter");
        registration.setOrder(-102);
        return registration;
    }


    @Order(-101)
    @ConfigurationProperties(prefix = "agileway.web.waf.allowed.methods")
    @Bean
    public FilterRegistrationBean allowedMethodsFilterRegistrationBean() {
        Environment env = SpringContextHolder.getApplicationContext().getEnvironment();
        String methodString = env.getProperty("agileway.web.waf.allowed.methods");
        String[] methods = Strings.split(methodString, ",");
        List<HttpMethod> allowedMethods = Pipeline.of(methods)
                .map(new Function<String, HttpMethod>() {
                    @Override
                    public HttpMethod apply(String method) {
                        return HttpMethod.resolve(Strings.upperCase(method));
                    }
                }).clearNulls().asList();
        if (Objs.isEmpty(allowedMethods)) {
            allowedMethods = Collects.newArrayList(HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.POST, HttpMethod.PATCH);
        }
        AllowedMethodsFilter allowedMethodsFilter = new AllowedMethodsFilter();
        allowedMethodsFilter.setAllowedMethods(allowedMethods);

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("AllowedMethods Filter");
        registration.setFilter(allowedMethodsFilter);
        registration.setOrder(-101);

        return registration;
    }


    @ConfigurationProperties(prefix = "agileway.web.waf.xss")
    @Bean
    public XssProperties xssProperties() {
        return new XssProperties();
    }


    @Order(-100)
    @Bean
    public FilterRegistrationBean xssFilterRegistrationBean(XssProperties xssProperties) {
        XssFirewall firewall = new XssWafFactory().get(xssProperties);
        XssFilter filter = new XssFilter();
        filter.setFirewall(firewall);

        firewall.init();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("XSS Filter");
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    @ConfigurationProperties(prefix = "agileway.web.set-header")
    @Bean
    public SetResponseHeaderProperties setResponseHeaderProperties() {
        return new SetResponseHeaderProperties();
    }

    @Order(-98)
    @Bean
    public FilterRegistrationBean setResponseHeadersRegistrationBean(SetResponseHeaderProperties properties) {

        SetResponseHeaderHandler handler = new SetResponseHeaderHandler();
        handler.setRules(properties.getRules());
        handler.init();

        HttpRequestHandlerFilter filter = new HttpRequestHandlerFilter();
        filter.setHandler(handler);

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("Set-Response-Header Filter");
        registration.setFilter(filter);
        registration.setOrder(-98);
        return registration;
    }

    @ConfigurationProperties(prefix = "agileway.web.waf.sql-injection")
    @Bean
    public SqlInjectionProperties sqlInjectProperties() {
        return new SqlInjectionProperties();
    }


    @Order(-97)
    @Bean
    public FilterRegistrationBean sqlInjectionRegistrationBean(SqlInjectionProperties sqlInjectProperties) {
        SqlFirewall firewall = new SqlInjectionWafFactory().get(sqlInjectProperties);
        SqlInjectionFilter filter = new SqlInjectionFilter();
        filter.setFirewall(firewall);

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("SQLInjection Filter");
        registration.setFilter(filter);
        registration.setOrder(-97);
        return registration;
    }

    @ConfigurationProperties(prefix = "agileway.web.waf.cors")
    @Bean
    public CorsProperties corsProperties() {
        return new CorsProperties();
    }

    @ConditionalOnProperty(prefix = "agileway.web.waf.cors", name = "enabeld", havingValue = "true")
    @Order(-96)
    @Bean
    public FilterRegistrationBean corsRegistrationBean(CorsProperties corsProperties) {
        corsProperties.setEnabled(true);
        CorsFilter corsFilter = new CorsFilter();
        corsFilter.setConf(corsProperties);

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setName("CORS Filter");
        registration.setFilter(corsFilter);
        registration.setOrder(-96);
        return registration;
    }

}
