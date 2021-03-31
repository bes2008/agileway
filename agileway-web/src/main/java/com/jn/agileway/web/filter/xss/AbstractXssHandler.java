package com.jn.agileway.web.filter.xss;

import com.jn.langx.lifecycle.AbstractInitializable;

public abstract class AbstractXssHandler extends AbstractInitializable implements XssHandler {

    @Override
    public String apply(String value) {
        if (isAttack(value)) {
            return "";
        }
        return value;
    }

    protected abstract boolean isAttack(String value);
}
