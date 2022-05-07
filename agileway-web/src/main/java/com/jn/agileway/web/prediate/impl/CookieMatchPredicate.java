package com.jn.agileway.web.prediate.impl;

import com.jn.agileway.web.prediate.HttpRequestPredicate;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.util.pattern.PatternMatcher;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieMatchPredicate implements HttpRequestPredicate {
    @NotEmpty
    private String name;
    private PatternMatcher matcher;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PatternMatcher getMatcher() {
        return matcher;
    }

    public void setMatcher(PatternMatcher matcher) {
        this.matcher = matcher;
    }

    @Override
    public boolean test(RR holder) {
        Cookie cookie = Servlets.getCookie((HttpServletRequest) holder.getRequest().getDelegate(), name);
        if(cookie!=null){
            return matcher==null || matcher.match(cookie.getValue());
        }
        return false;
    }
}
