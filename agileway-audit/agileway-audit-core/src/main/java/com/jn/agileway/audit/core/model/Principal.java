package com.jn.agileway.audit.core.model;

import com.jn.langx.util.collection.CommonProps;

public class Principal extends CommonProps {
    public static final long serialVersionUID = 1L;

    private String principalId;// {required} the user id
    private String principalName;// {required} the user name
    private String principalType;// {required} the user type
    private String clientIp;    // {optional} ip
    private String clientHost; // {optional} ip
    private int clientPort;     // {optional} port

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    public String getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(String principalType) {
        this.principalType = principalType;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public String getClientHost() {
        return clientHost;
    }

    public void setClientHost(String clientHost) {
        this.clientHost = clientHost;
    }
}
