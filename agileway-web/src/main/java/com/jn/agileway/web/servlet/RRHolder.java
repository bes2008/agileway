package com.jn.agileway.web.servlet;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.langx.annotation.Singleton;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class RRHolder {
    private static final RRHolder instance = new RRHolder();

    private RRHolder() {

    }

    public static RRHolder getInstance() {
        return RRHolder.instance;
    }

    public static HttpServletResponse getResponse() {
        HttpResponse response = RRLocal.getResponse();
        if (response != null) {
            return (HttpServletResponse) response.getDelegate();
        }
        return null;
    }

    public static HttpServletRequest getRequest() {
        HttpRequest request = RRLocal.getRequest();
        if (request != null) {
            return (HttpServletRequest) request.getDelegate();
        }
        return null;
    }

    public static void set(final HttpServletRequest request, final HttpServletResponse response) {
        HttpRequest req = null;
        HttpResponse resp = null;
    }

    public static RR get() {
        return RRLocal.get();
    }

    public static void remove() {
        RRLocal.remove();
    }
}
