package com.jn.agileway.web.filter.rr;

import javax.servlet.http.*;

public class RRHolder {
    private static final RRHolder instance;
    private static final ThreadLocal<RR> rr;

    static {
        rr = new ThreadLocal<RR>();
        instance = new RRHolder();
    }

    public static RRHolder getInstance() {
        return RRHolder.instance;
    }

    public static HttpServletResponse getResponse() {
        return rr.get().getResponse();
    }

    public static HttpServletRequest getRequest() {
        return rr.get().getRequest();
    }

    public static void set(final HttpServletRequest request, final HttpServletResponse response) {
        rr.set(new RR(request, response));
    }

    public static void remove() {
        rr.remove();
    }


    private static class RR {
        private HttpServletRequest request;
        private HttpServletResponse response;

        public RR(final HttpServletRequest request, final HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }

        public HttpServletRequest getRequest() {
            return this.request;
        }

        public HttpServletResponse getResponse() {
            return this.response;
        }
    }
}