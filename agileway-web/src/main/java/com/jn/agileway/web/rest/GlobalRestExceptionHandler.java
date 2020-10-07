package com.jn.agileway.web.rest;

import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.Initializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.jn.agileway.web.rest.GlobalRestHandlers.GLOBAL_REST_EXCEPTION_HANDLER;
import static com.jn.agileway.web.rest.GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN;

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

    private String defaultErrorCode = "UNKNOWN";
    private String defaultErrorMessage = "UNKNOWN";
    private volatile boolean inited = false;
    private volatile boolean running = false;
    private boolean writeUnifiedResponse = true;
    private GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry;
    private RestErrorMessageHandler errorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;

    public void setJsonFactory(JSONFactory jsonFactory) {
        if (jsonFactory != null) {
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
        Object exceptionHandler = request.getAttribute(GLOBAL_REST_EXCEPTION_HANDLER);
        if (exceptionHandler != null) {
            return null;
        }
        Boolean globalRestResponseHadWritten = (Boolean) request.getAttribute(GLOBAL_REST_RESPONSE_HAD_WRITTEN);
        if (globalRestResponseHadWritten != null && globalRestResponseHadWritten) {
            return null;
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
            request.setAttribute(GLOBAL_REST_EXCEPTION_HANDLER, this);
            errorMessageHandler.handler(respBody);

            if (writeUnifiedResponse) {
                try {
                    response.setStatus(respBody.getStatusCode());
                    String jsonstring = jsonFactory.get().toJson(respBody);
                    response.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
                    response.setCharacterEncoding(Charsets.UTF_8.name());
                    response.getWriter().write(jsonstring);
                    request.setAttribute(GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);
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
            logger.info("Initial the global rest exception handler {}", Reflects.getFQNClassName(getClass()));
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
        if (Strings.isNotBlank(defaultErrorCode)) {
            this.defaultErrorCode = defaultErrorCode;
        }
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        if (Strings.isNotBlank(defaultErrorMessage)) {
            this.defaultErrorMessage = defaultErrorMessage;
        }
    }


    public void setWriteUnifiedResponse(boolean writeUnifiedResponse) {
        this.writeUnifiedResponse = writeUnifiedResponse;
    }

}
