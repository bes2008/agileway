package com.jn.agileway.httpclient.auth.inject;

import com.jn.langx.io.resource.PathMatcher;
import com.jn.langx.util.pattern.patternset.AntPathMatcher;

public class PathRequestMatcher<R> implements RequestMatcher<R> {

    private CredentialsInjectionContext context;
    protected PathMatcher pathMatcher;

    public PathRequestMatcher(String pathPatterns) {
        this(new AntPathMatcher(pathPatterns));
    }

    public PathRequestMatcher(PathMatcher pathMatcher) {
        this(pathMatcher, null);
    }

    public PathRequestMatcher(PathMatcher pathMatcher, CredentialsInjectionContext context) {
        this.pathMatcher = pathMatcher;
        this.context = context;
    }

    public void setContext(CredentialsInjectionContext context) {
        this.context = context;
    }

    @Override
    public Boolean matches(R httpRequest) {
        String url = context.getUrlGetter().getUrl(httpRequest);
        if (url.startsWith(context.getBaseUri())) {
            String path = url.substring(context.getBaseUri().length());
            return pathMatcher.matches(path);
        } else {
            return false;
        }

    }
}
