package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.lifecycle.Lifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 通常在 Controller层调用
 */
public abstract class GlobalRestExceptionHandler implements RestActionExceptionHandler, Initializable, Lifecycle {
    private static Logger logger = LoggerFactory.getLogger(GlobalRestExceptionHandler.class);
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);
    /**
     * 是否根据 异常链扫描
     * <p>
     * 这个配置存在的意义: 有人喜欢把真正的异常用RuntimeException包装
     */
    private boolean causeScanEnabled = false;

    /**
     * 默认的出错时的 http status code
     */
    private int defaultErrorStatusCode;

    private String defaultErrorCode;
    private String defaultErrorMessage;
    private volatile boolean inited = false;
    private volatile boolean running = false;
    private boolean writeUnifiedResponse = true;
    private GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry;
    private RestErrorMessageHandler errorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;

    public void setJsonFactory(JSONFactory jsonFactory) {
        if(jsonFactory!=null) {
            this.jsonFactory = jsonFactory;
        }
    }

    public void setExceptionHandlerRegistry(GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry) {
        this.exceptionHandlerRegistry = exceptionHandlerRegistry;
    }

    public RestRespBody handle(HttpServletRequest request, HttpServletResponse response, Object action, Exception ex) {
        if (!inited) {
            init();
        }

        RestRespBody respBody = null;

        if (isSupportedRestAction(request, response, action, ex)) {

            // 找那些注册好了的异常处理
            RestActionExceptionHandlerRegistration restHandlerExceptionResolverRegistration = exceptionHandlerRegistry.findExceptionResolver(ex, causeScanEnabled);


            if (restHandlerExceptionResolverRegistration != null) {
                respBody = restHandlerExceptionResolverRegistration.getExceptionHandler().handle(request, response, action, ex);
                if (respBody == null) {
                    RestActionExceptionHandlerDefinition element = restHandlerExceptionResolverRegistration.findMatchedRegistration(ex, causeScanEnabled);
                    RestRespBody.error(element.getDefaultStatusCode(), element.getDefaultErrorCode(), element.getDefaultErrorMessage());
                }
            }

            if (respBody == null) {
                logger.error(ex.getMessage(), ex);
                respBody = RestRespBody.error(defaultErrorStatusCode, defaultErrorCode, defaultErrorMessage);
            }

            errorMessageHandler.handler(respBody);

            if (writeUnifiedResponse) {
                try {
                    response.setStatus(respBody.getStatusCode());
                    String jsonstring = jsonFactory.get().toJson(respBody);
                    response.getWriter().write(jsonstring);
                } catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
                }
            }
        }
        return respBody;
    }


    protected abstract boolean isSupportedRestAction(HttpServletRequest request, HttpServletResponse response, Object action, Exception ex);


    @Override
    public void init() throws InitializationException {
        if (!inited) {
            inited = true;
            exceptionHandlerRegistry.init();
        }
    }

    @Override
    public void startup() {
        init();
        if (!running) {
            running = true;
        }
    }

    @Override
    public void shutdown() {
        running = false;
    }

    public void setErrorMessageHandler(RestErrorMessageHandler errorMessageHandler) {
        if (errorMessageHandler != null) {
            this.errorMessageHandler = errorMessageHandler;
        }
    }

    public void setCauseScanEnabled(boolean causeScanEnabled) {
        this.causeScanEnabled = causeScanEnabled;
    }

    public void setDefaultErrorStatusCode(int defaultErrorStatusCode) {
        this.defaultErrorStatusCode = defaultErrorStatusCode;
    }

    public void setDefaultErrorCode(String defaultErrorCode) {
        this.defaultErrorCode = defaultErrorCode;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        this.defaultErrorMessage = defaultErrorMessage;
    }


    public void setWriteUnifiedResponse(boolean writeUnifiedResponse) {
        this.writeUnifiedResponse = writeUnifiedResponse;
    }

}
