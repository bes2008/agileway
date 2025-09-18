package com.jn.agileway.httpclient.auth.inject;

public class CredentialsInjectionContext {
    private String baseUri;
    private RequestUrlGetter urlGetter;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public RequestUrlGetter getUrlGetter() {
        return urlGetter;
    }

    public void setUrlGetter(RequestUrlGetter urlGetter) {
        this.urlGetter = urlGetter;
    }
}
