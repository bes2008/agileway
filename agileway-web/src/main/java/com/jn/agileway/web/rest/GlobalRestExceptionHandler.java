package com.jn.agileway.web.rest;

import com.jn.agileway.http.rest.GlobalRestExceptionHandlerRegistry;
import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rest.RestActionExceptionHandlerDefinition;
import com.jn.agileway.http.rest.RestActionExceptionHandlerRegistration;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.lifecycle.Lifecycle;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;

import static com.jn.agileway.http.rest.GlobalRestHandlers.GLOBAL_REST_EXCEPTION_HANDLER;
import static com.jn.agileway.http.rest.GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN;

/**
 * 通常在 Controller层调用
 */
public abstract class GlobalRestExceptionHandler extends AbstractGlobalRestResponseHandler<Object, Exception> implements Lifecycle {
    private static Logger logger = Loggers.getLogger(GlobalRestExceptionHandler.class);

    private volatile boolean running = false;
    private GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry;

    public void setExceptionHandlerRegistry(GlobalRestExceptionHandlerRegistry exceptionHandlerRegistry) {
        this.exceptionHandlerRegistry = exceptionHandlerRegistry;
    }

    public RestRespBody handle(HttpRequest request, HttpResponse response, Object action, Exception ex) {
        init();
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
            boolean causeScanEnabled = context.getExceptionHandlerProperties().isCauseScanEnabled();
            RestActionExceptionHandlerRegistration restHandlerExceptionResolverRegistration = exceptionHandlerRegistry.findExceptionResolver(ex, causeScanEnabled);

            if (restHandlerExceptionResolverRegistration != null) {
                respBody = restHandlerExceptionResolverRegistration.getExceptionHandler().handle(request, response, action, ex);
                if (respBody == null) {
                    RestActionExceptionHandlerDefinition element = restHandlerExceptionResolverRegistration.findMatchedRegistration(ex, causeScanEnabled);
                    respBody = RestRespBody.error(element.getDefaultStatusCode(), element.getDefaultErrorCode(), element.getDefaultErrorMessage());
                }
            }

            if (respBody == null) {
                respBody = RestRespBody.error(context.getDefaultRestErrorMessageHandler().getDefaultErrorStatusCode(), context.getDefaultRestErrorMessageHandler().getDefaultErrorCode(), context.getDefaultRestErrorMessageHandler().getDefaultErrorMessage());
            }

            if (getContext().getExceptionHandlerProperties().isLogStack()) {
                logger.error(ex.getMessage(), ex);
            }
            request.setAttribute(GLOBAL_REST_EXCEPTION_HANDLER, this);

            Map<String, Object> finalBody = toMap(request, response, action, respBody);

            if (context.getExceptionHandlerProperties().isWriteUnifiedResponse()) {
                try {
                    if (!response.isCommitted()) {
                        response.resetBuffer();
                        response.setStatus(respBody.getStatusCode());
                        String jsonstring = context.getJsonFactory().get().toJson(finalBody);

                        response.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
                        response.setCharacterEncoding(Charsets.UTF_8.name());
                        response.getWriter().write(jsonstring);
                        request.setAttribute(GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);
                    }
                } catch (IOException ioe) {
                    logger.warn(ioe.getMessage(), ioe);
                }
            }
        }
        return respBody;
    }

    protected abstract boolean isSupportedRestAction(HttpRequest request, HttpResponse response, Object action, Exception ex);


    @Override
    public void doInit() {
        if (!inited) {
            logger.info("===[AGILE_WAY-GLOBAL_REST_EXCEPTION_HANDLER]=== Initial the global rest exception handler: {}", Reflects.getFQNClassName(getClass()));
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

}
