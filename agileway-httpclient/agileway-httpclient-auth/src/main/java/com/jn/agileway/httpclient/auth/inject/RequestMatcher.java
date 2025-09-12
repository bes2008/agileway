package com.jn.agileway.httpclient.auth.inject;

import com.jn.langx.Matcher;

public interface RequestMatcher<R> extends Matcher<R, Boolean> {
    @Override
    Boolean matches(R httpRequest);
}
