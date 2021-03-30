package com.jn.agileway.web.filter.xss;

import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.util.function.Function;

public interface XssHandler extends Function<String, String>, Initializable {
    @Override
    String apply(String value);
}
