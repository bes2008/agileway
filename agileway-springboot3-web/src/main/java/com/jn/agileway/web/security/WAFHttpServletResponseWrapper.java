package com.jn.agileway.web.security;

import com.jn.langx.util.Objs;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
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
