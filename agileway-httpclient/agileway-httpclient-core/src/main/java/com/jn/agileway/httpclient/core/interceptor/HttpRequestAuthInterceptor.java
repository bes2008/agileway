package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.auth.AuthCredentialsInjector;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

public class HttpRequestAuthInterceptor implements HttpRequestInterceptor {
    private final GenericRegistry<AuthCredentialsInjector> registry;

    public HttpRequestAuthInterceptor(GenericRegistry<AuthCredentialsInjector> registry) {
        this.registry = registry;
    }

    @Override
    public void intercept(HttpRequest request) {
        AuthCredentialsInjector injector = Pipeline.of(registry.instances())
                .findFirst(new Predicate<AuthCredentialsInjector>() {
                    @Override
                    public boolean test(AuthCredentialsInjector serviceCredentialsInjector) {
                        return serviceCredentialsInjector.getRequestMatcher().matches(request);
                    }
                });
        if (injector != null) {
            injector.inject(request);
        }
    }
}
