package com.jn.agileway.httpclient.auth.inject;

import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class ServiceCredentialsInjector<R> implements CredentialsInjector<R> {
    private String serviceName;
    private CredentialsInjectionContext context;

    private RequestMatcher<R> matcher;
    private List<CredentialsInjector<R>> injectors = Lists.newArrayList();

    public ServiceCredentialsInjector(String name, String baseUri, RequestUrlGetter<R> urlGetter, CredentialsInjector<R>... injectors) {
        this(name, new CredentialsInjectionContext(baseUri, urlGetter), injectors);
    }

    public ServiceCredentialsInjector(String name, CredentialsInjectionContext context, CredentialsInjector<R>... injectors) {
        this.serviceName = name;
        this.matcher = new BaseUriMatcher();
        Pipeline.of(injectors).clearEmptys().addTo(this.injectors);
        setContext(context);
    }

    @Override
    public RequestMatcher<R> getRequestMatcher() {
        return this.matcher;
    }

    @Override
    public void inject(R httpRequest) {

        if (Boolean.FALSE.equals(this.matcher.matches(httpRequest)) || injectors.isEmpty()) {
            return;
        }
        CredentialsInjector<R> injector = Pipeline.of(injectors).findFirst(new Predicate<CredentialsInjector<R>>() {
            @Override
            public boolean test(CredentialsInjector<R> injector) {
                return injector.getRequestMatcher().matches(httpRequest);
            }
        });
        if (injector != null) {
            injector.inject(httpRequest);
        }
    }

    @Override
    public String getName() {
        return serviceName;
    }

    class BaseUriMatcher implements RequestMatcher<R> {
        @Override
        public Boolean matches(R httpRequest) {
            String url = getContext().getUrlGetter().getUrl(httpRequest);
            return url.startsWith(getContext().getBaseUri());
        }
    }

    @Override
    public CredentialsInjectionContext getContext() {
        return context;
    }

    @Override
    public void setContext(CredentialsInjectionContext context) {
        if (context == null) {
            return;
        }
        this.context = context;
        Pipeline.of(this.injectors).forEach(new Consumer<CredentialsInjector<R>>() {
            @Override
            public void accept(CredentialsInjector<R> injector) {
                injector.setContext(context);
            }
        });
    }
}
