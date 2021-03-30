package com.jn.agileway.web.filter.xss;

import com.jn.langx.lifecycle.InitializationException;

public abstract class AbstractXssHandler implements XssHandler {
    private volatile boolean inited = false;

    @Override
    public void init() throws InitializationException {
        if (!inited) {
            doInit();
            inited = true;
        }
    }

    protected void doInit() throws InitializationException{

    }

    @Override
    public String apply(String value) {
        if (isAttack(value)) {
            return "";
        }
        return value;
    }

    protected abstract boolean isAttack(String value);
}
