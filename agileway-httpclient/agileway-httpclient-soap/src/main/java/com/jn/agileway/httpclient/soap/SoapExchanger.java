package com.jn.agileway.httpclient.soap;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.langx.util.concurrent.promise.Promise;

public class SoapExchanger {
    private HttpExchanger httpExchanger;

    public SoapExchanger(HttpExchanger exchanger) {
        this.httpExchanger = exchanger;
    }

    public <T> HttpResponse<T> exchange(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, binding, soapMessage, expectedContentType).await();
    }

    public <T> Promise<HttpResponse<T>> exchangeAsync(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        HttpRequest request = HttpRequest.forPost(uri, null, null, soapMessage);
        request.getHeaders().setContentType(binding.getContentType());
        return httpExchanger.exchange(true, request, expectedContentType);
    }


}
