package com.jn.agileway.web.rest;


import com.jn.agileway.web.security.WAFs;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.Objs;
import com.jn.langx.util.logging.Loggers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    public abstract RestRespBody handle(HttpServletRequest request, HttpServletResponse response, ACTION action, ACTION_RESULT actionReturnValue);

    @Override
    public Map<String, Object> toMap(HttpServletRequest request, HttpServletResponse response, ACTION action, RestRespBody respBody) {
        if (respBody == null) {
            return null;
        }
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
            respBody.setMethod(Servlets.getMethod(request));
        }
        if (!context.getConfiguration().isIgnoredField(RestRespBody.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
            respBody.withRequestHeaders(Servlets.headersToMultiValueMap(request));
        }

        Map<String, Object> map = context.getResponseBodyMapper().apply(respBody);
        return map;
    }


}
