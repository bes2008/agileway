package com.jn.agileway.web.security;

import com.jn.langx.util.Objs;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.List;

public class WAFHttpServletResponseWrapper extends HttpServletResponseWrapper {
    private List<String> httpOnlyCookies;

    public void setHttpOnlyCookies(List<String> httpOnlyCookies) {
        this.httpOnlyCookies = httpOnlyCookies;
    }

    public WAFHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void addCookie(Cookie cookie) {
        if (Objs.isNotEmpty(httpOnlyCookies)) {
            if (httpOnlyCookies.contains(cookie.getName())) {
                cookie.setHttpOnly(true);
            }
        }
        super.addCookie(cookie);
    }
}
