package com.jn.agileway.web.filter.waf.xss;

import com.jn.agileway.web.filter.waf.AbstractWAFHandler;

public abstract class AbstractXssHandler extends AbstractWAFHandler {


    @Override
    public final String getAttackName() {
        return "XSS";
    }
}
