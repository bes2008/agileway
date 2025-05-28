package com.jn.agileway.httpclient.soap.entity;

import java.net.URI;
import java.util.Map;

public class SoapFaultV12 implements SoapFault {
    private String code;
    String subCode;
    private String reason;
    private URI role;
    private URI node;
    private Map<String, String> detail;

    public SoapFaultV12() {
    }

    public SoapFaultV12(String code, String subCode, String reason, URI role, URI node, Map<String, String> detail) {
        this.code = code;
        this.subCode = subCode;
        this.reason = reason;
        this.role = role;
        this.node = node;
        this.detail = detail;
    }

    @Override
    public Map<String, String> getDetail() {
        return this.detail;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getSubCode() {
        return this.subCode;
    }

    @Override
    public String getReason() {
        return this.reason;
    }

    @Override
    public URI getRole() {
        return this.role;
    }
}
