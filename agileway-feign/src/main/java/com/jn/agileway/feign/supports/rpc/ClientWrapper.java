package com.jn.agileway.feign.supports.rpc;

import com.jn.langx.util.struct.ThreadLocalHolder;
import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;

public class ClientWrapper implements Client {

    public static ThreadLocalHolder<FeignRR> feignRRHolder = new ThreadLocalHolder<FeignRR>();

    private Client delegate;

    public ClientWrapper(Client client) {
        this.delegate = client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        FeignRR feignRR = new FeignRR();
        feignRR.setRequest(request);
        feignRRHolder.set(feignRR);
        Response response = this.delegate.execute(request, options);
        feignRR.setResponse(response);
        return response;
    }
}
