package com.jn.agileway.httpclient.core;

import com.jn.agileway.eipchannel.core.message.MessageHeaders;
import com.jn.langx.util.Strings;
import com.jn.langx.util.net.http.HttpHeaders;
import com.jn.langx.util.net.http.HttpMethod;

import java.net.URI;

/**
 * 代表了Http响应，它是提供给用户直接使用的.
 *
 * <pre>
 *   <code>
 *      HttpResponse response = httpExchanger.exchange(...).await();
 *      if(response.hasError()){
 *          System.out.println(response.getErrorMessage());
 *      }else{
 *          System.out.println(response.getData());
 *      }
 *   </code>
 * </pre>
 *
 * @param <T>
 */
public class HttpResponse<T> extends BaseHttpMessage<T> implements HttpResponseMessage<T> {
    private int statusCode;
    private String errorMessage;

    public HttpResponse(HttpMethod method, URI uri, int statusCode, HttpHeaders headers, String errorMessage, T data) {
        this(method, uri, statusCode, toMessageHeaders(headers), errorMessage, data);
    }

    public HttpResponse(HttpMethod method, URI uri, int statusCode, MessageHeaders<HttpHeaders> headers, String errorMessage, T data) {
        this.uri = uri;
        this.method = method;
        this.statusCode = statusCode;
        this.headers = headers;
        this.errorMessage = errorMessage;
        this.payload = data;
    }

    private static MessageHeaders<HttpHeaders> toMessageHeaders(HttpHeaders headers) {
        MessageHeaders<HttpHeaders> messageHeaders = new MessageHeaders<HttpHeaders>();
        messageHeaders.setProtocolHeaders(headers);
        return messageHeaders;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean hasError() {
        return statusCode >= 400 || Strings.isNotEmpty(errorMessage);
    }
}
