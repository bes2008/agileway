package com.jn.agileway.httpclient.soap;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapMessage;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.promise.Promise;

import java.util.Map;

public class SoapExchanger {
    private HttpExchanger httpExchanger;

    public SoapExchanger(HttpExchanger exchanger) {
        this.httpExchanger = exchanger;
    }

    public <T> Promise<HttpResponse<T>> exchange(boolean async, String uri, MultiValueMap<String, Object> queryParams, Map<String, Object> uriVariables, SoapMessage soapMessage, Class<T> expectedContentType) {
        HttpRequest request = HttpRequest.forPost(uri, queryParams, uriVariables, soapMessage);
        return httpExchanger.exchange(async, request, expectedContentType);
    }

}
