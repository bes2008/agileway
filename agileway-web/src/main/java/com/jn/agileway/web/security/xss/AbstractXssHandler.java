package com.jn.agileway.web.security.xss;

import com.jn.agileway.web.security.AbstractWAFHandler;

public abstract class AbstractXssHandler extends AbstractWAFHandler {


    @Override
    public final String getAttackName() {
        return "XSS";
    }
}
