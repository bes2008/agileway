package com.jn.agileway.http.rr;


public class RR {
    private HttpRequest request;
    private HttpResponse response;

    public RR(final HttpRequest request, final HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpRequest getRequest() {
        return this.request;
    }

    public HttpResponse getResponse() {
        return this.response;
    }

    public void setRequest(HttpRequest request){
        this.request = request;
    }
    public void setResponse(HttpResponse response){
        this.response = response;
    }
}
