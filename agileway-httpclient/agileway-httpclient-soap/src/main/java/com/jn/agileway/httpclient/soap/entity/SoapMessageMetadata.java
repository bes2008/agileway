package com.jn.agileway.httpclient.soap.entity;

import static com.jn.agileway.httpclient.soap.utils.SOAPs.NAMESPACE_PREFIX_DEFAULT;

public class SoapMessageMetadata {
    private SoapBinding version;
    /**
     * 用于自定义命名空间前缀，默认为 "agilewaysoap"
     */
    private String namespacePrefix;

    public SoapMessageMetadata() {
        this(SoapBinding.SOAP12_HTTP, NAMESPACE_PREFIX_DEFAULT);
    }

    public SoapMessageMetadata(SoapBinding version, String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
        this.version = version;
    }

    public SoapBinding getVersion() {
        return version;
    }

    public void setVersion(SoapBinding version) {
        this.version = version;
    }

    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }
}
