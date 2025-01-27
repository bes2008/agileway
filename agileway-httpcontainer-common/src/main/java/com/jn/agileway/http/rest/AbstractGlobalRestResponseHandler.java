package com.jn.agileway.http.rest;


import com.jn.agileway.http.rr.HttpRRs;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.logging.Loggers;

import java.util.Map;

public abstract class AbstractGlobalRestResponseHandler<ACTION, ACTION_RESULT> extends AbstractInitializable implements GlobalRestResponseBodyHandler<ACTION, ACTION_RESULT> {
    protected GlobalRestResponseBodyContext context;

    @Override
    public void setContext(GlobalRestResponseBodyContext context) {
        this.context = context;
    }

    @Override
    public GlobalRestResponseBodyContext getContext() {
        return context;
    }

    @Override
    public abstract RestRespBody handle(HttpRequest request, HttpResponse response, ACTION action, ACTION_RESULT actionReturnValue);

    @Override
    public Map<String, Object> toMap(HttpRequest request, HttpResponse response, ACTION action, RestRespBody respBody) {
        if (respBody == null) {
            return null;
        }
        beforeConvert(request, response, action, respBody);
        Map<String, Object> map = context.getResponseBodyMapper().apply(respBody);
        return map;
    }

    protected void beforeConvert(HttpRequest request, HttpResponse response, ACTION action, RestRespBody respBody){
        if (respBody.getStatusCode() >= 400) {
            try {
                context.getRestErrorMessageHandler().handler(request.getLocale(), respBody);
            } catch (Throwable ex1) {
                Loggers.getLogger(getClass()).error(ex1.getMessage(), ex1);
            } finally {
                context.getDefaultRestErrorMessageHandler().handler(request.getLocale(), respBody);
            }
        }

        /*
        String xssFilteredData = WAFs.clearIfContainsJavaScript(context.getJsonFactory().get().toJson(respBody.getData()));
        if (Objs.isEmpty(xssFilteredData)) {
            respBody.setData(null);
        }
         */
        if (!context.getConfiguration().isIgnoredField(RestRespBody.GLOBAL_REST_FIELD_URL)) {
            respBody.setUrl(request.getRequestURL().toString());
        }
        if (!context.getConfiguration().isIgnoredField(RestRespBody.GLOBAL_REST_FIELD_METHOD)) {
            respBody.setMethod(HttpRRs.getMethod(request));
        }
        if (!context.getConfiguration().isIgnoredField(RestRespBody.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
            respBody.withRequestHeaders(HttpRRs.headersToMultiValueMap(request));
        }

    }

    protected void writeResponse(HttpRequest request, HttpResponse response, ACTION action, RestRespBody respBody){}

}
