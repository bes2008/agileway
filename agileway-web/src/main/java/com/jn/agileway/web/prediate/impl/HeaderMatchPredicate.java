package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.rr.RR;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;

import java.util.regex.Pattern;

public class HeaderMatchPredicate implements HttpRequestPredicate {
    @NotEmpty
    private String header;
    /**
     * 该值如果为 null，则表示 判断该 header是否存在
     */
    @Nullable
    private Pattern valuePattern;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Pattern getValuePattern() {
        return valuePattern;
    }

    public void setValuePattern(Pattern valuePattern) {
        this.valuePattern = valuePattern;
    }

    @Override
    public boolean test(RR rr) {
        if (this.valuePattern == null) {
            return rr.getRequest().getHeader(header) != null;
        }
        return Pipeline.<String>of(rr.getRequest().getHeaders(header)).anyMatch(new Predicate<String>() {
            @Override
            public boolean test(String value) {
                return valuePattern.matcher(value).matches();
            }
        });
    }
}
