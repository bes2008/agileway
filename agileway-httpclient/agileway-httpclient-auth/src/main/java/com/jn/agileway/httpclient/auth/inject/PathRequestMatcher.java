package com.jn.agileway.httpclient.auth.inject;

import java.nio.file.PathMatcher;

public class PathRequestMatcher<R> implements RequestMatcher<R> {
    private RequestUrlGetter<R> urlGetter;
    protected PathMatcher pathMatcher;

    public PathRequestMatcher(PathMatcher pathMatcher) {
        this.pathMatcher = pathMatcher;
    }

    @Override
    public Boolean matches(R httpRequest) {
        String url = urlGetter.getUrl(httpRequest);
        return pathMatcher.matches(url);
    }
}
