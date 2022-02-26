package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.security.WAFs;
import com.jn.agileway.web.rest.*;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.net.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

public class GlobalFilterRestResponseHandler extends AbstractGlobalRestResponseBodyHandler<Method> {
    private GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties = new GlobalRestExceptionHandlerProperties();
    private final DefaultRestErrorMessageHandler defaultRestErrorMessageHandler = new DefaultRestErrorMessageHandler();


    @Override
    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }

    @Override
    public void setJsonFactory(JSONFactory jsonFactory) {
        if (jsonFactory != null) {
            this.jsonFactory = jsonFactory;
        }
    }

    @Override
    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setRestErrorMessageHandler(RestErrorMessageHandler restErrorMessageHandler) {
        if (restErrorMessageHandler != null) {
            this.restErrorMessageHandler = restErrorMessageHandler;
        }
    }

    public void setGlobalRestExceptionHandlerProperties(GlobalRestExceptionHandlerProperties globalRestExceptionHandlerProperties) {
        if (globalRestExceptionHandlerProperties != null) {
            this.globalRestExceptionHandlerProperties = globalRestExceptionHandlerProperties;
            this.defaultRestErrorMessageHandler.setDefaultErrorCode(globalRestExceptionHandlerProperties.getDefaultErrorCode());
            this.defaultRestErrorMessageHandler.setDefaultErrorMessage(globalRestExceptionHandlerProperties.getDefaultErrorMessage());
            this.defaultRestErrorMessageHandler.setDefaultErrorStatusCode(globalRestExceptionHandlerProperties.getDefaultErrorStatusCode());
        }
    }

    @Override
    public RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, Method method, Object actionReturnValue) {
        // 是否是非rest请求，或者禁用了全局Rest处理的请求，都视为是 非Rest请求
        Boolean nonRestRequest = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST);
        if (nonRestRequest != null && nonRestRequest) {
            return null;
        }

        int statusCode = response.getStatus();
        long contentLength = Servlets.getContentLength(response);
        // 这个==0的判断其实没啥用
        if (contentLength == 0) {
            boolean error = HttpStatus.is4xxClientError(statusCode) || HttpStatus.is5xxServerError(statusCode);
            if (error) {
                //rest response body 是否已写过
                Boolean responseBodyWritten = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN);
                if ((responseBodyWritten == null || !responseBodyWritten) && !response.isCommitted()) {
                    RestRespBody respBody = new RestRespBody(false, statusCode, "", this.defaultRestErrorMessageHandler.getDefaultErrorCode(), this.defaultRestErrorMessageHandler.getDefaultErrorMessage());
                    if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_URL)) {
                        respBody.setUrl(request.getRequestURL().toString());
                    }
                    restErrorMessageHandler.handler(request.getLocale(), respBody);
                    defaultRestErrorMessageHandler.handler(request.getLocale(), respBody);

                    try {
                        if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
                            respBody.withRequestHeaders(Servlets.headersToMultiValueMap(request));
                        }
                        if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_METHOD)) {
                            respBody.setMethod(Servlets.getMethod(request));
                        }


                        String datajson = jsonFactory.get().toJson(respBody.getData());
                        String xssFilteredData = WAFs.clearIfContainsJavaScript(datajson);
                        if (Objs.isEmpty(xssFilteredData)) {
                            respBody.setData(null);
                        }
                    //    Map<String,Object> result = respBody.toMap();
                    //    String json = jsonFactory.get().toJson(json);
                        response.resetBuffer();
                        Servlets.writeToResponse(response, GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8, json);
                    } catch (IOException ex) {
                        Throwables.throwAsRuntimeException(ex);
                    }
                    return respBody;
                }
            }
        }
        return null;
    }
}
