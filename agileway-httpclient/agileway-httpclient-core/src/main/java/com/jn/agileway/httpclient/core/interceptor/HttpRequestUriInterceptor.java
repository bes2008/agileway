package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpRequestInterceptor;
import com.jn.agileway.httpclient.core.exception.BadHttpRequestException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.exclusion.IncludeExcludePredicate;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.function.Predicate2;
import com.jn.langx.util.regexp.Regexp;
import com.jn.langx.util.regexp.Regexps;

import java.net.URI;
import java.util.List;

/**
 * 对 uri 进行校验
 */
public class HttpRequestUriInterceptor implements HttpRequestInterceptor {
    private List<String> supportedSchemes;
    private List<Regexp> allowedAuthorities;
    private List<Regexp> notAllowedAuthorities;

    public HttpRequestUriInterceptor(List<String> supportedSchemes, List<String> allowedAuthorities, List<String> notAllowedAuthorities) {
        setSupportedSchemes(supportedSchemes);
        setAllowedAuthorities(allowedAuthorities);
        setNotAllowedAuthorities(notAllowedAuthorities);
    }

    public void setSupportedSchemes(List<String> supportedSchemes) {
        List<String> defaultSchemes = Lists.immutableList("http", "https", "ws", "wss");
        supportedSchemes = Pipeline.of(supportedSchemes)
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return defaultSchemes.contains(s);
                    }
                }).asList();
        if (Objs.isEmpty(supportedSchemes)) {
            this.supportedSchemes = defaultSchemes;
        } else {
            this.supportedSchemes = supportedSchemes;
        }
    }

    public void setAllowedAuthorities(List<String> allowedAuthorities) {
        Pipeline.of(allowedAuthorities).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                addAllowedAuthority(s);
            }
        });
    }

    public void setNotAllowedAuthorities(List<String> notAllowedAuthorities) {
        Pipeline.of(notAllowedAuthorities).forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                addNotAllowedAuthority(s);
            }
        });
    }

    public void addAllowedAuthority(String pattern) {
        if (Strings.isNotBlank(pattern)) {
            Regexp regexp = Regexps.compile(pattern);
            if (allowedAuthorities == null) {
                allowedAuthorities = Lists.newArrayList();
            }
            allowedAuthorities.add(regexp);
        }
    }

    public void addNotAllowedAuthority(String pattern) {
        if (Strings.isNotBlank(pattern)) {
            Regexp regexp = Regexps.compile(pattern);
            if (notAllowedAuthorities == null) {
                notAllowedAuthorities = Lists.newArrayList();
            }
            notAllowedAuthorities.add(regexp);
        }
    }

    @Override
    public void intercept(HttpRequest request) {
        URI uri = request.getUri();
        if (uri == null) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "Http request uri is required");
        }
        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "Http request uri scheme is required");
        }
        if (!supportedSchemes.contains(scheme)) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "Http request uri scheme is not supported: " + scheme);
        }

        String authority = uri.getAuthority();
        if (!isAllowedAuthority(authority)) {
            throw new BadHttpRequestException(request.getMethod(), request.getUri(), "Http request uri authority is not allowed: " + authority);
        }
    }

    private boolean isAllowedAuthority(String authority) {
        Predicate2<Regexp, String> regexpPredicate = new Predicate2<Regexp, String>() {
            @Override
            public boolean test(Regexp regexp, String authority) {
                return Regexps.match(regexp, authority);
            }
        };
        return new IncludeExcludePredicate<Regexp, String>(this.allowedAuthorities, regexpPredicate, this.notAllowedAuthorities, regexpPredicate).test(authority);
    }
}
