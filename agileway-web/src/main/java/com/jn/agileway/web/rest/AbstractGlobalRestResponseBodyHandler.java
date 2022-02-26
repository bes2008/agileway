package com.jn.agileway.web.rest;

import com.jn.agileway.web.security.WAFs;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.Objs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractGlobalRestResponseBodyHandler<ACTION> extends AbstractInitializable implements GlobalRestResponseBodyHandler<ACTION> {

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
    public abstract RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue);

    @Override
    public Map<String, Object> toMap(HttpServletRequest request, HttpServletResponse response, ACTION action, RestRespBody respBody) {
        if (respBody == null) {
            return null;
        }
        if (respBody.getStatusCode() >= 400) {
            context.getRestErrorMessageHandler().handler(request.getLocale(), respBody);
        }

        String xssFilteredData = WAFs.clearIfContainsJavaScript(context.getJsonFactory().get().toJson(respBody.getData()));
        if (Objs.isEmpty(xssFilteredData)) {
            respBody.setData(null);
        }

        if (!context.getConfiguration().isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_URL)) {
            respBody.setUrl(request.getRequestURL().toString());
        }
        if (!context.getConfiguration().isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_METHOD)) {
            respBody.setMethod(Servlets.getMethod(request));
        }
        if (!context.getConfiguration().isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
            respBody.withRequestHeaders(Servlets.headersToMultiValueMap(request));
        }

        Map<String, Object> map = context.getResponseBodyMapper().apply(respBody);
        return map;
    }
}
