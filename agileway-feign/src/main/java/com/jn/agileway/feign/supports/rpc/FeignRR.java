package com.jn.agileway.feign.supports.rpc;

import feign.Request;
import feign.Response;

public class FeignRR {
    private Request request;
    private Response response;

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }
}
