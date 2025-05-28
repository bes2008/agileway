package com.jn.agileway.httpclient.soap.entity;

import static com.jn.agileway.httpclient.soap.utils.SOAPs.NAMESPACE_PREFIX_DEFAULT;

public class SoapMessageMetadata {
    private SoapVersion version;
    /**
     * 用于自定义命名空间前缀，默认为 "agilewaysoap"
     */
    private String namespacePrefix;

    public SoapMessageMetadata() {
        this(SoapVersion.V1_2, NAMESPACE_PREFIX_DEFAULT);
    }

    public SoapMessageMetadata(SoapVersion version, String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
        this.version = version;
    }

    public SoapVersion getVersion() {
        return version;
    }

    public void setVersion(SoapVersion version) {
        this.version = version;
    }

    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }
}
