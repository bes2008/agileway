package com.jn.agileway.http.authc.bearer;

import com.jn.agileway.http.authc.Credentials;

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
