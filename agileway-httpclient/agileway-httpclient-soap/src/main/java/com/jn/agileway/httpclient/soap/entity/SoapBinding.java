package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.util.Strings;
import com.jn.langx.util.net.mime.MediaType;

public enum SoapBinding {
    SOAP11_HTTP("SOAP11_HTTP", "http://schemas.xmlsoap.org/soap/envelope", MediaType.TEXT_XML),
    SOAP12_HTTP("SOAP12_HTTP", "http://www.w3.org/2003/05/soap-envelope", MediaType.APPLICATION_SOAP12_XML);
    private String name;
    private String namespaceUri;
    private MediaType contentType;

    SoapBinding(String name, String namespaceUri, MediaType contentType) {
        this.name = name;
        this.namespaceUri = namespaceUri;
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public String getNamespaceUri() {
        return namespaceUri;
    }

    public MediaType getContentType() {
        return contentType;
    }

    public static SoapBinding getByContentType(MediaType contentType) {
        if (contentType != null) {
            for (SoapBinding binding : values()) {
                if (binding.contentType.equalsTypeAndSubtype(contentType)) {
                    return binding;
                }
            }
        }
        return null;
    }

    public static SoapBinding getByNamespaceUri(String namespaceUri) {
        if (Strings.isBlank(namespaceUri)) {
            return null;
        }
        for (SoapBinding binding : values()) {
            if (binding.namespaceUri.equals(namespaceUri)) {
                return binding;
            }
        }
        return null;
    }
}
