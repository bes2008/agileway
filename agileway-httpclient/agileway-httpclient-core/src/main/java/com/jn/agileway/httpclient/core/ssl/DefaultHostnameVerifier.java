package com.jn.agileway.httpclient.core.ssl;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.collection.exclusion.IncludeExcludePredicate;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.net.HostAndPort;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.net.Proxy;
import java.util.List;

public class DefaultHostnameVerifier implements HostnameVerifier {
    private List<String> allowedHostnames;
    private List<String> notAllowedHostnames;
    private Proxy proxy;

    public DefaultHostnameVerifier(List<String> allowedAuthorities, List<String> notAllowedAuthorities, Proxy proxy) {
        Function<String, String> getHostnameFunction = new Function<String, String>() {
            @Override
            public String apply(String s) {
                return getHostname(s);
            }
        };
        this.allowedHostnames = Pipeline.of(allowedAuthorities).map(getHostnameFunction).asList();
        this.notAllowedHostnames = Pipeline.of(notAllowedHostnames).map(getHostnameFunction).asList();
        if (proxy.type() == Proxy.Type.HTTP) {
            this.proxy = proxy;
        }
    }

    private String getHostname(String authority) {
        HostAndPort hostAndPort = HostAndPort.of(authority);
        return hostAndPort.getName();
    }

    @Override
    public boolean verify(String hostname, SSLSession sslSession) {

        if (proxy != null) {
            if (Strings.equals(hostname, proxy.address().toString())) {
                return true;
            }
        }

        return new IncludeExcludePredicate<String, String>(allowedHostnames, notAllowedHostnames).test(hostname);
    }
}
