package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.annotation.NonNull;

import java.net.URI;
import java.util.Map;

public class SoapFaultV11 implements SoapFault {
    @NonNull
    private String code;
    @NonNull
    private String reason;
    private URI actor;
    private Map<String, String> detail;

    public SoapFaultV11() {
    }

    public SoapFaultV11(String faultCode, String faultString, URI faultActor, Map<String, String> detail) {
        this.code = faultCode;
        this.reason = faultString;
        this.actor = faultActor;
        this.detail = detail;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public URI getActor() {
        return actor;
    }

    public void setActor(URI actor) {
        this.actor = actor;
    }

    public Map<String, String> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, String> detail) {
        this.detail = detail;
    }

    @Override
    public String getSubCode() {
        return null;
    }

    @Override
    public URI getRole() {
        return this.actor;
    }
}
