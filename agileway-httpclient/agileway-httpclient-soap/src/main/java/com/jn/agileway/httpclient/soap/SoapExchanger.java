package com.jn.agileway.httpclient.soap;

import com.jn.agileway.httpclient.Exchanger;
import com.jn.agileway.httpclient.core.HttpExchanger;
import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.soap.entity.SoapBinding;
import com.jn.agileway.httpclient.soap.plugin.SoapFaultResponseExtractor;
import com.jn.langx.util.collection.multivalue.MultiValueMap;
import com.jn.langx.util.concurrent.promise.Promise;
import com.jn.langx.util.net.http.HttpHeaders;

import java.lang.reflect.Type;
import java.util.Map;

public class SoapExchanger implements Exchanger {
    private HttpExchanger httpExchanger;
    private SoapFaultResponseExtractor soapFaultResponseExtractor = new SoapFaultResponseExtractor();

    public SoapExchanger(HttpExchanger exchanger) {
        this.httpExchanger = exchanger;
    }

    public <T> HttpResponse<T> exchange(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, binding, soapMessage, expectedContentType).await();
    }

    public <T> HttpResponse<T> exchange(String uri, MultiValueMap<String, Object> queryParams, Map<String, Object> uriVariables, SoapBinding binding, HttpHeaders headers, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, queryParams, uriVariables, headers, binding, soapMessage, expectedContentType).await();
    }

    public <T> Promise<HttpResponse<T>> exchangeAsync(String uri, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        return this.exchangeAsync(uri, null, null, null, binding, soapMessage, expectedContentType);
    }

    public <T> Promise<HttpResponse<T>> exchangeAsync(String uri, MultiValueMap<String, Object> queryParams, Map<String, Object> uriVariables, HttpHeaders headers, SoapBinding binding, Object soapMessage, Class<T> expectedContentType) {
        HttpRequest request = HttpRequest.forPost(uri, null, queryParams, uriVariables, headers, soapMessage);
        request.getHttpHeaders().setContentType(binding.getContentType());
        return exchange(true, request, expectedContentType);
    }

    @Override
    public <T> Promise<HttpResponse<T>> exchange(boolean async, HttpRequest<?> request, Type expectedContentType) {
        return httpExchanger.exchange(async, request, expectedContentType, null, soapFaultResponseExtractor);
    }

}
