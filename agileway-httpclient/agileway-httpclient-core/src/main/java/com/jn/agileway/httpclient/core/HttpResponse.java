package com.jn.agileway.httpclient.core;

import com.jn.agileway.httpclient.core.underlying.UnderlyingHttpResponse;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.io.IOs;

import java.io.IOException;
import java.io.InputStream;

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
public class HttpResponse<T> extends BaseHttpMessage<T> {
    private int statusCode;
    private String errorMessage;

    public HttpResponse(UnderlyingHttpResponse response) {
        this(response, null);
    }

    public HttpResponse(UnderlyingHttpResponse response, T data) {
        this(response, data, false);
    }

    public HttpResponse(UnderlyingHttpResponse response, T data, boolean readIfDataAbsent) {
        this.uri = response.getUri();
        this.method = response.getMethod();
        this.statusCode = response.getStatusCode();
        this.headers = response.getHeaders();

        if (data != null) {
            this.payload = data;
        } else if (readIfDataAbsent) {
            try {
                InputStream inputStream = response.getPayload();
                if (inputStream != null) {
                    byte[] bytes = IOs.toByteArray(inputStream);
                    if (statusCode >= 400) {
                        this.errorMessage = new String(bytes, Charsets.UTF_8);
                    } else {
                        this.payload = (T) bytes;
                    }
                }
            } catch (IOException e) {
                throw Throwables.wrapAsRuntimeIOException(e);
            }
        }
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
