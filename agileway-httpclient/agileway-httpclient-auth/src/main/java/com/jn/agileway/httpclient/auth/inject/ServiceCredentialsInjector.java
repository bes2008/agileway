package com.jn.agileway.httpclient.auth.inject;

import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class ServiceCredentialsInjector<R> implements CredentialsInjector<R> {
    private String serviceName;
    private String baseUri;
    private RequestUrlGetter<R> urlGetter;

    private RequestMatcher<R> matcher;
    private List<CredentialsInjector<R>> injectors = Lists.newArrayList();

    public ServiceCredentialsInjector(String name, String baseUri, RequestUrlGetter<R> urlGetter, CredentialsInjector<R>... injectors) {
        this.serviceName = name;
        this.baseUri = baseUri;
        this.urlGetter = urlGetter;
        this.matcher = new BaseUriMatcher();
        Pipeline.of(injectors).addTo(this.injectors);
    }

    @Override
    public RequestMatcher<R> getRequestMatcher() {
        return this.matcher;
    }

    @Override
    public void inject(R httpRequest) {

        if (!this.matcher.matches(httpRequest) || injectors.isEmpty()) {
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
            String url = urlGetter.getUrl(httpRequest);
            return url.startsWith(baseUri);
        }
    }
}
