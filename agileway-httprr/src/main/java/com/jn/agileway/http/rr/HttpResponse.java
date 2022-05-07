package com.jn.agileway.http.rr;

import java.util.Collection;

public interface HttpResponse<D> {
    D getDelegate();
    void addHeader(String name, String value);
    Collection<String> getHeaderNames();
    Collection<String> getHeaders(String name);
}
