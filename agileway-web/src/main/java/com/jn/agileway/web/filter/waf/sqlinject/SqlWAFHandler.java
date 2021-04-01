package com.jn.agileway.web.filter.waf.sqlinject;

import com.jn.agileway.web.filter.waf.AbstractWAFHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 替换掉特殊字符
 */
public abstract class SqlWAFHandler extends AbstractWAFHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


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
}
