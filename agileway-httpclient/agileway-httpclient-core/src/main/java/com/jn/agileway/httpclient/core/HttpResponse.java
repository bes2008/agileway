package com.jn.agileway.httpclient.core;

import com.jn.langx.util.Strings;

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

    public HttpResponse(HttpResponseMessage response, String errorMessage, T data) {
        this.uri = response.getUri();
        this.method = response.getMethod();
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();
        this.errorMessage = errorMessage;
        this.payload = data;
    }


    public int getStatusCode() {
        return this.statusCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean hasError() {
        return statusCode >= 400 || Strings.isNotEmpty(errorMessage);
    }
}
