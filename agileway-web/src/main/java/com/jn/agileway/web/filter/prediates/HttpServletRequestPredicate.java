package com.jn.agileway.web.filter.prediates;

import com.jn.langx.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

public interface HttpServletRequestPredicate extends Predicate<HttpServletRequest> {
    @Override
    boolean test(HttpServletRequest request);
}
