package com.jn.agileway.http.rr;

public interface HttpRequest<D> {
    D getDelegate();
    String getRemoteHost();
    String getMethod();
    String getRequestURI();
    String getHeader(String name);
}
