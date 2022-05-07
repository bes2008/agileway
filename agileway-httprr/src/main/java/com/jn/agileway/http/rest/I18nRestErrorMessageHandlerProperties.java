package com.jn.agileway.http.rest;

public class I18nRestErrorMessageHandlerProperties {

    /**
     * 是否强制使用 I18n storage里提供的错误消息
     */
    private boolean forceI18n = true;

    public boolean isForceI18n() {
        return forceI18n;
    }

    public void setForceI18n(boolean forceI18n) {
        this.forceI18n = forceI18n;
    }
}
