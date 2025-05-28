package com.jn.agileway.httpclient.soap.entity;

import java.net.URI;
import java.util.Map;

public class SoapFault {
    /**
     * SOAP Fault code, V1.1, V1.2都有，名字不一样
     */
    private String code;
    /**
     * SOAP Fault sub code, V1.2才有
     */
    String subCode;
    /**
     * SOAP Fault reason, V1.1, V1.2都有
     */
    private String reason;
    /**
     * SOAP Fault role, actor, V1.1, V1.2都有，名字不一样
     */
    private URI role;
    /**
     * SOAP Fault node, V1.2才有
     */
    private URI node;
    /**
     * SOAP Fault detail, V1.1, V1.2都有
     */
    private Map<String, String> detail;

    public SoapFault() {
    }

    public SoapFault(String code, String subCode, String reason, URI role, URI node, Map<String, String> detail) {
        this.code = code;
        this.subCode = subCode;
        this.reason = reason;
        this.role = role;
        this.node = node;
        this.detail = detail;
    }

    public Map<String, String> getDetail() {
        return this.detail;
    }

    public String getCode() {
        return this.code;
    }

    public String getSubCode() {
        return this.subCode;
    }

    public String getReason() {
        return this.reason;
    }

    public URI getRole() {
        return this.role;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setRole(URI role) {
        this.role = role;
    }

    public URI getNode() {
        return node;
    }

    public void setNode(URI node) {
        this.node = node;
    }

    public void setDetail(Map<String, String> detail) {
        this.detail = detail;
    }
}
