package com.jn.agileway.httpclient.auth.bearer;

import com.jn.agileway.httpclient.auth.Credentials;

public class BearerCredentials implements Credentials {
    private String token;

    public BearerCredentials(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
