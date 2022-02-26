package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.lifecycle.AbstractInitializable;

public class GlobalRestResponseBodyContext extends AbstractInitializable {
    private JSONFactory jsonFactory;

    private GlobalRestResponseBodyHandlerProperties handlerProperties;
    private GlobalRestResponseBodyHandlerConfiguration configuration;

    private GlobalRestResponseBodyMapper responseBodyMapper;

    private RestErrorMessageHandler restErrorMessageHandler;
    private GlobalRestExceptionHandlerProperties exceptionHandlerProperties;
    private final DefaultRestErrorMessageHandler defaultRestErrorMessageHandler = new DefaultRestErrorMessageHandler();

    @Override
    protected void doInit() {
        if (jsonFactory == null) {
            jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
        }

        if (configuration == null) {
            configuration = new GlobalRestResponseBodyHandlerConfiguration();
        }
        if (responseBodyMapper == null) {
            responseBodyMapper = new GlobalRestResponseBodyMapper(configuration);
        }

        if (restErrorMessageHandler == null) {
            restErrorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;
        }

    }

    public GlobalRestResponseBodyHandlerProperties getHandlerProperties() {
        return handlerProperties;
    }

    public void setHandlerProperties(GlobalRestResponseBodyHandlerProperties handlerProperties) {
        this.handlerProperties = handlerProperties;
    }

    public GlobalRestResponseBodyHandlerConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
    }

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public RestErrorMessageHandler getRestErrorMessageHandler() {
        return restErrorMessageHandler;
    }

    public void setRestErrorMessageHandler(RestErrorMessageHandler restErrorMessageHandler) {
        this.restErrorMessageHandler = restErrorMessageHandler;
    }

    public GlobalRestResponseBodyMapper getResponseBodyMapper() {
        return responseBodyMapper;
    }

    public void setResponseBodyMapper(GlobalRestResponseBodyMapper responseBodyMapper) {
        this.responseBodyMapper = responseBodyMapper;
    }

    public GlobalRestExceptionHandlerProperties getExceptionHandlerProperties() {
        return exceptionHandlerProperties;
    }

    public void setExceptionHandlerProperties(GlobalRestExceptionHandlerProperties exceptionHandlerProperties) {
        if (exceptionHandlerProperties != null) {
            this.exceptionHandlerProperties = exceptionHandlerProperties;
            this.defaultRestErrorMessageHandler.setDefaultErrorCode(exceptionHandlerProperties.getDefaultErrorCode());
            this.defaultRestErrorMessageHandler.setDefaultErrorMessage(exceptionHandlerProperties.getDefaultErrorMessage());
            this.defaultRestErrorMessageHandler.setDefaultErrorStatusCode(exceptionHandlerProperties.getDefaultErrorStatusCode());
        }
    }

    public DefaultRestErrorMessageHandler getDefaultRestErrorMessageHandler() {
        return defaultRestErrorMessageHandler;
    }

}
