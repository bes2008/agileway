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

    private RRHolder(){

    }
    public static RRHolder getInstance() {
        return RRHolder.instance;
    }

    public static HttpServletResponse getResponse() {
        return RRLocal.getInstance().getResponse();
    }

    public static HttpServletRequest getRequest() {
        return rr.get().getRequest();
    }

    public static void set(final HttpRequest request, final HttpServletResponse response) {
        rr.set(new RR(request, response));
    }

    public static RR get(){
        return rr.get();
    }

    public static void remove() {
        rr.remove();
    }
}
