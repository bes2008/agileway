package com.jn.agileway.web.security;

import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

public abstract class AbstractWAFHandler extends AbstractInitializable implements WAFHandler {
    private final Logger logger = Loggers.getLogger(this.getClass());

    @Override
    public String apply(String value) {
        if (isAttack(value)) {
            logger.warn("maybe {} attack: {}", getAttackName(), value);
            return "";
        }
        return value;
    }

    protected abstract boolean isAttack(String value);
}
