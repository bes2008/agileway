package com.jn.agileway.http.rr;

import com.jn.langx.annotation.Singleton;

@Singleton
public class RRLocal {
    private static final RRLocal instance;
    private static final ThreadLocal<RR> rr;

    static {
        rr = new ThreadLocal<RR>();
        instance = new RRLocal();
    }

    public static RRLocal getInstance() {
        return RRLocal.instance;
    }

    public static HttpResponse getResponse() {
        RR r = rr.get();
        if (r != null) {
            return r.getResponse();
        }
        return null;
    }

    public static HttpRequest getRequest() {
        RR r = rr.get();
        if (r != null) {
            return r.getRequest();
        }
        return null;
    }

    public static void set(final HttpRequest request, final HttpResponse response) {
        RR r = rr.get();
        if (r == null) {
            rr.set(new RR(request, response));
        } else {
            r.setRequest(request);
            r.setResponse(response);
        }
    }

    public static RR get() {
        return rr.get();
    }

    public static void remove() {
        rr.remove();
    }

}