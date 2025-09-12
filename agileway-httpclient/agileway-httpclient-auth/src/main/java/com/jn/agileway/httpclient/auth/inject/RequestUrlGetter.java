package com.jn.agileway.httpclient.auth.inject;

public interface RequestUrlGetter<R> {
    String getUrl(R request);
}
