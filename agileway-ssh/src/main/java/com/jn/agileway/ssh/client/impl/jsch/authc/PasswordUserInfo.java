package com.jn.agileway.ssh.client.impl.jsch.authc;

import com.jcraft.jsch.UserInfo;

public class PasswordUserInfo implements UserInfo {
    private String passphrase;
    private String password;

    public void setPassphrase(String passphrase) {
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

    /**
     * @param message
     * @return 返回是否拿到密码
     */
    @Override
    public boolean promptPassword(String message) {
        return true;
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
