package com.jn.agileway.http.rr;

import java.util.Enumeration;

public interface HttpRequest<D> {
    D getDelegate();
    String getRemoteHost();
    String getMethod();
    String getRequestURI();
    String getHeader(String name);
    Enumeration<String> getHeaders(String name);
}
