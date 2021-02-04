package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.UserInfo;

public class SimpleUserInfo implements UserInfo {
    private String passphrase;
    private String password;

    public void setPassphrase(String passphrase){
        this.passphrase = passphrase;
    }

    @Override
    public String getPassphrase() {
        return this.passphrase;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean promptPassword(String message) {
        return false;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return false;
    }

    @Override
    public boolean promptYesNo(String message) {
        return false;
    }

    @Override
    public void showMessage(String message) {

    }
}
