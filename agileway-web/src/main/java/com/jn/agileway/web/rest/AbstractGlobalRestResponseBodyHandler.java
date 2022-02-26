package com.jn.agileway.web.rest;

import com.jn.agileway.web.security.WAFs;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.Objs;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractGlobalRestResponseBodyHandler<ACTION> extends AbstractInitializable implements GlobalRestResponseBodyHandler<ACTION> {
    protected GlobalRestResponseBodyHandlerConfiguration configuration;
    protected JSONFactory jsonFactory;
    protected RestErrorMessageHandler restErrorMessageHandler;
    protected GlobalRestResponseBodyMapper resultMapper;

    public void setRestErrorMessageHandler(RestErrorMessageHandler restErrorMessageHandler) {
        this.restErrorMessageHandler = restErrorMessageHandler;
    }

    public void setResultMapper(GlobalRestResponseBodyMapper resultMapper) {
        this.resultMapper = resultMapper;
    }

    @Override
    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
    }

    public GlobalRestResponseBodyHandlerConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    @Override
    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    @Override
    protected void doInit() {
        if (jsonFactory == null) {
            jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
        }
        if (restErrorMessageHandler == null) {
            restErrorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;
        }
        if (configuration == null) {
            configuration = new GlobalRestResponseBodyHandlerConfiguration();
        }
        if (resultMapper == null) {
            resultMapper = new GlobalRestResponseBodyMapper(configuration);
        }
    }

    @Override
    public abstract RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue);

    @Override
    public Map<String, Object> toMap(HttpServletRequest request, HttpServletResponse response, ACTION action, RestRespBody respBody) {
        if (respBody == null) {
            return null;
        }
        if (respBody.getStatusCode() >= 400) {
            restErrorMessageHandler.handler(request.getLocale(), respBody);
        }

        String xssFilteredData = WAFs.clearIfContainsJavaScript(jsonFactory.get().toJson(respBody.getData()));
        if (Objs.isEmpty(xssFilteredData)) {
            respBody.setData(null);
        }

        if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_URL)) {
            respBody.setUrl(request.getRequestURL().toString());
        }
        if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_METHOD)) {
            respBody.setMethod(Servlets.getMethod(request));
        }
        if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
            respBody.withRequestHeaders(Servlets.headersToMultiValueMap(request));
        }

        Map<String, Object> map = resultMapper.apply(respBody);
        return map;
    }
}
