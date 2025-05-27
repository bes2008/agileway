package com.jn.agileway.httpclient.soap.entity;

import com.jn.langx.annotation.NonNull;

import java.util.Map;

public class SoapFault {
    @NonNull
    private String faultCode;
    @NonNull
    private String faultString;
    private String faultActor;
    private Map<String, String> detail;

    public SoapFault() {
    }

    public SoapFault(String faultCode, String faultString, String faultActor, Map<String, String> detail) {
        this.faultCode = faultCode;
        this.faultString = faultString;
        this.faultActor = faultActor;
        this.detail = detail;
    }

    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }

    public String getFaultActor() {
        return faultActor;
    }

    public void setFaultActor(String faultActor) {
        this.faultActor = faultActor;
    }

    public Map<String, String> getDetail() {
        return detail;
    }

    public void setDetail(Map<String, String> detail) {
        this.detail = detail;
    }
}
