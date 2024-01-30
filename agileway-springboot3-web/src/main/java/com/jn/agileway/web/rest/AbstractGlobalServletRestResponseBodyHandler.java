package com.jn.agileway.web.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestResponseHandler;
import com.jn.agileway.http.rr.HttpRRs;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.web.security.WAFs;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.logging.Loggers;


public abstract class AbstractGlobalServletRestResponseBodyHandler<ACTION> extends AbstractGlobalRestResponseHandler<ACTION, Object> {
    @Override
    public abstract RestRespBody handle(HttpRequest request, HttpResponse response, ACTION action, Object actionReturnValue);

    protected void beforeConvert(HttpRequest request, HttpResponse response, ACTION action, RestRespBody respBody) {
        if (respBody.getStatusCode() >= 400) {
            try {
                context.getRestErrorMessageHandler().handler(request.getLocale(), respBody);
            } catch (Throwable ex1) {
                Loggers.getLogger(getClass()).error(ex1.getMessage(), ex1);
            } finally {
                context.getDefaultRestErrorMessageHandler().handler(request.getLocale(), respBody);
            }
        }

        String xssFilteredData = WAFs.clearIfContainsJavaScript(context.getJsonFactory().get().toJson(respBody.getData()));
        if (Objs.isEmpty(xssFilteredData)) {
            respBody.setData(null);
        }
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
}
