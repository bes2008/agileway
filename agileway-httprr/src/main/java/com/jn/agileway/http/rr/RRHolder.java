package com.jn.agileway.http.rr;

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

    public static HttpResponse getResponse() {
        return rr.get().getResponse();
    }

    public static HttpRequest getRequest() {
        return rr.get().getRequest();
    }

    public static void set(final HttpRequest request, final HttpResponse response) {
        rr.set(new RR(request, response));
    }

    public static RR get(){
        return rr.get();
    }

    public static void remove() {
        rr.remove();
    }

}