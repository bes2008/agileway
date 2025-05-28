package com.jn.agileway.httpclient.soap.entity;

import static com.jn.agileway.httpclient.soap.utils.SOAPs.NAMESPACE_PREFIX_DEFAULT;

public class SoapMessageMetadata {
    private SoapBinding binding;
    /**
     * 用于自定义命名空间前缀，默认为 "agilewaysoap"
     */
    private String namespacePrefix;

    public SoapMessageMetadata() {
        this(SoapBinding.SOAP12_HTTP, NAMESPACE_PREFIX_DEFAULT);
    }

    public SoapMessageMetadata(SoapBinding version, String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
        this.binding = version;
    }

    public SoapBinding getBinding() {
        return binding;
    }

    public void setBinding(SoapBinding binding) {
        this.binding = binding;
    }

    public String getNamespacePrefix() {
        return namespacePrefix;
    }

    public void setNamespacePrefix(String namespacePrefix) {
        this.namespacePrefix = namespacePrefix;
    }
}
