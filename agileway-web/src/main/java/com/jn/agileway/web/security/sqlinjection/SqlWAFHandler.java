package com.jn.agileway.web.security.sqlinjection;

import com.jn.agileway.web.security.AbstractWAFHandler;

/**
 * 替换掉特殊字符
 */
public abstract class SqlWAFHandler extends AbstractWAFHandler {

    @Override
    public abstract String apply(String value);

    @Override
    public String getAttackName() {
        return "SQL-Inject";
    }

    @Override
    protected boolean isAttack(String value) {
        return false;
    }

    @Override
    public boolean requestHeaderAware() {
        return false;
    }
}
