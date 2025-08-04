package com.jn.agileway.httpclient.auth.basic;

import com.jn.agileway.httpclient.auth.Credentials;

public class UserPasswordCredentials implements Credentials {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserPasswordCredentials() {

    }

    public UserPasswordCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
