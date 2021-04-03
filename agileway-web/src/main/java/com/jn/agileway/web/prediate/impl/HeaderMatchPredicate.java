package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.util.regex.Pattern;

public class HeaderMatchPredicate implements HttpRequestPredicate {
    @NotEmpty
    private String header;
    @Nullable
    private Pattern valuePattern;

    @Override
    public boolean test(RR rr) {
        return Pipeline.<String>of(rr.getRequest().getHeaders(header)).anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String value) {
                return valuePattern.matcher(value).matches();
            }
        });
    }
}
