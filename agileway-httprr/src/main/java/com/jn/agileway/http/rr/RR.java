package com.jn.agileway.http.rr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RR {
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

    public void setRequest(HttpServletRequest request){
        this.request = request;
    }
    public void setResponse(HttpServletResponse response){
        this.response = response;
    }
}
