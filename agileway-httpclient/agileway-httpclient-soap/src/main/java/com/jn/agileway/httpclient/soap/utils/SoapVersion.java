package com.jn.agileway.httpclient.soap.utils;

public enum SoapVersion {
    V1_1("1.1", "https://schemas.xmlsoap.org/soap/envelope/"),
    V1_2("1.2", "https://www.w3.org/2003/05/soap-envelope/");
    private String name;
    private String namespaceUri;

    SoapVersion(String name, String namespaceUri) {
        this.name = name;
        this.namespaceUri = namespaceUri;
    }

    public String getName() {
        return name;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }
}
