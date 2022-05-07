package com.jn.agileway.http.rr;

public interface HttpResponse<D> {
    D getDelegate();
    void addHeader(String name, String value);
}
