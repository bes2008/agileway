package com.jn.agileway.httpclient.auth.inject;

public class CredentialsInjectionContext {
    private String baseUri;
    private RequestUrlGetter urlGetter;

    public CredentialsInjectionContext(String baseUri, RequestUrlGetter urlGetter) {
        this.baseUri = baseUri;
        this.urlGetter = urlGetter;
    }

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
