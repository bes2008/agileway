package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.rest.RestRespBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractGlobalRestResponseBodyHandler<ACTION> implements GlobalRestResponseBodyHandler<ACTION>{
    protected GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfiguration();
    protected JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
    protected RestErrorMessageHandler restErrorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;

    @Override
    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
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
    public abstract RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, ACTION action, Object actionReturnValue) ;
}
