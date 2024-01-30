package com.jn.agileway.web.security.xss;

import com.jn.agileway.web.security.AbstractWAFHandler;

public abstract class AbstractXssHandler extends AbstractWAFHandler {

    @Override
    public boolean requestHeaderAware() {
        return true;
    }

    @Override
    public final String getAttackName() {
        return "XSS";
    }
}
