package com.jn.agileway.jwt.tests.nacos_jwt;

/**
 * Nacos User.
 *
 * @author nkorange
 * @since 1.2.0
 */
public class NacosUser extends User {

    private String token;

    private boolean globalAdmin = false;

    public NacosUser() {
    }

    public NacosUser(String userName) {
        setUserName(userName);
    }

    public NacosUser(String userName, String token) {
        setUserName(userName);
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isGlobalAdmin() {
        return globalAdmin;
    }

    public void setGlobalAdmin(boolean globalAdmin) {
        this.globalAdmin = globalAdmin;
    }

    @Override
    public String toString() {
        return "NacosUser{" + "token='" + token + '\'' + ", globalAdmin=" + globalAdmin + '}';
    }
}