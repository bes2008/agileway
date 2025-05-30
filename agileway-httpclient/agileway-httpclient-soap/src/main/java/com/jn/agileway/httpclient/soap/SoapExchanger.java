package com.jn.agileway.httpclient.soap;

import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.agileway.httpclient.soap.plugin.SoapFaultResponseExtractor;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.promise.Promise;

import java.util.Map;

public class SoapExchanger {
    private HttpExchanger httpExchanger;
    private SoapFaultResponseExtractor soapFaultResponseExtractor = new SoapFaultResponseExtractor();

    public SoapExchanger(HttpExchanger exchanger) {
        this.httpExchanger = exchanger;
    }

    public <T> HttpResponse<T> exchange(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, binding, soapMessage, expectedContentType).await();
    }

    public <T> HttpResponse<T> exchange(String uri, MultiValueMap<String, Object> queryParams, Map<String, Object> uriVariables, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, queryParams, uriVariables, binding, soapMessage, expectedContentType).await();
    }

    public <T> Promise<HttpResponse<T>> exchangeAsync(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, null, null, binding, soapMessage, expectedContentType);
    }

    public <T> Promise<HttpResponse<T>> exchangeAsync(String uri, MultiValueMap<String, Object> queryParams, Map<String, Object> uriVariables, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        HttpRequest request = HttpRequest.forPost(uri, queryParams, uriVariables, soapMessage);
        request.getHeaders().setContentType(binding.getContentType());
        return httpExchanger.exchange(true, request, expectedContentType, null, soapFaultResponseExtractor);
    }


}
