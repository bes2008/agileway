package com.jn.agileway.httpclient.soap;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapMessage;
import com.jn.langx.util.concurrent.promise.Promise;

public class SoapExchanger {
    private HttpExchanger httpExchanger;

    public SoapExchanger(HttpExchanger exchanger) {
        this.httpExchanger = exchanger;
    }

    public <T> Promise<HttpResponse<T>> exchange(boolean async, String uri, SoapMessage soapMessage, Class<T> expectedContentType) {
        HttpRequest request = HttpRequest.forPost(uri, null, null, soapMessage);
        return httpExchanger.exchange(async, request, expectedContentType);
    }

}
