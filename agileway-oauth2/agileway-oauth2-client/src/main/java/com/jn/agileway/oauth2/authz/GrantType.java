package com.jn.agileway.oauth2.authz;

public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password"),
    IMPLICIT("implicit");

    private String name;

    GrantType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
