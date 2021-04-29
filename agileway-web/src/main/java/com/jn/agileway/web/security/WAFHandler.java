package com.jn.agileway.web.security;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.util.function.Function;

public interface WAFHandler extends Function<String, String>, Initializable {
    @Override
    String apply(String value);

    String getAttackName();

    /**
     * 是否处理请求头
     *
     * @return
     */
    boolean requestHeaderAware();
}
