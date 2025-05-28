package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.util.Objs;

import static com.jn.agileway.httpclient.soap.utils.SOAPs.NAMESPACE_PREFIX_DEFAULT;

public class SoapMessageMetadata {
    private SoapBinding binding;
    /**
     * 用于自定义命名空间前缀，默认为 "agilewaysoap"
     */
    private String namespacePrefix;

    public SoapMessageMetadata() {
        this(SoapBinding.SOAP12_HTTP);
    }

    public SoapMessageMetadata(SoapBinding binding) {
        this(binding, null);
    }

    public SoapMessageMetadata(SoapBinding binding, String namespacePrefix) {
        this.namespacePrefix = Objs.useValueIfEmpty(namespacePrefix, NAMESPACE_PREFIX_DEFAULT);
        this.binding = binding;
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
