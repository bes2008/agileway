package com.jn.agileway.http.rr;

import java.util.Collection;

public interface HttpResponse<D> {
    D getContainerResponse();
    void addHeader(String name, String value);
    String getHeader(String name);
    Collection<String> getHeaderNames();
    Collection<String> getHeaders(String name);
    int getStatusCode();
}
