package com.jn.agileway.jwt.tests.nacos_jwt;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -8002966873087151367L;

    /**
     * Unique string representing user.
     */
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}