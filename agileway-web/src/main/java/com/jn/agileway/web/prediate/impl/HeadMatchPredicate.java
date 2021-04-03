package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.web.servlet.RR;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;

public class HeadMatchPredicate implements HttpRequestPredicate {
    @NotEmpty
    private String header;
    @Nullable
    private String valuePattern;

    @Override
    public boolean test(RR rr) {
        return false;
    }
}
