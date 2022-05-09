package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestResponseHandler;
import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessor;
import com.jn.agileway.http.rr.requestmapping.RequestMappingAccessorRegistry;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.struct.Pair;
import org.slf4j.Logger;

import javax.ws.rs.core.Response;
import java.lang.reflect.Method;
import java.util.List;

public class JaxrsGlobalRestResponseBodyHandler extends AbstractGlobalRestResponseHandler {
    private static final Logger logger = Loggers.getLogger(JaxrsGlobalRestResponseBodyHandler.class);
    private RequestMappingAccessorRegistry requestMappingAccessorRegistry;

    public void setRequestMappingAccessorRegistry(RequestMappingAccessorRegistry requestMappingAccessorRegistry) {
        this.requestMappingAccessorRegistry = requestMappingAccessorRegistry;
    }

    /**
     * 执行完毕 http请求处理方法后，立马执行该方法
     */
    public void judgeIsSupportedAction(HttpRequest request, Method actionMethod, Object actionReturnValue) {
        boolean isRestRequest = true;
        if (actionReturnValue instanceof com.jn.langx.io.resource.Resource) {
            isRestRequest = false;
        } else {
            isRestRequest = isSupportedAction(actionMethod);
        }
        request.setAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST, !isRestRequest);
        if (isRestRequest) {
            request.setAttribute(GlobalRestHandlers.GLOBAL_REST_ACTION_METHOD, actionMethod);
        }
    }

    public boolean isSupportedAction(Method actionMethod) {
        if (actionMethod == null) {
            return false;
        }
        boolean supported = getContext().getConfiguration().isAcceptable(actionMethod);
        if (supported && requestMappingAccessorRegistry != null) {
            Pair<Method, RequestMappingAccessor> pair = requestMappingAccessorRegistry.get(actionMethod);
            RequestMappingAccessor requestMappingAccessor = pair.getValue();
            if (requestMappingAccessor == null) {
                // 没有 @RequestMapping
                supported = false;
            } else {
                List<String> produces = requestMappingAccessor.produces();
                if (Objs.isNotEmpty(produces)) {
                    if (Collects.containsNone(produces, Collects.newArrayList("*/*", "application/*", "application/json"))) {
                        supported = false;
                    }
                }
            }
        }
        if (!supported) {
            if (logger.isDebugEnabled()) {
                logger.debug("{} is not supported for unified response body handler", actionMethod.toString());
            }
        }
        return supported;
    }


    @Override
    public RestRespBody handle(HttpRequest request, HttpResponse response, Object actionMethod, Object actionReturnValue) {
        RestRespBody respBody = convertToRestRespBody(request, response, actionMethod, actionReturnValue);
        return respBody;
    }

    private RestRespBody convertToRestRespBody(HttpRequest request, HttpResponse response, Object actionMethod, Object body) {
        int statusCode = -1;
        RestRespBody respBody = null;

        statusCode = extractStatusCode(body, response);

        if (body instanceof Response) {
            body = ((Response) body).getEntity();
        }

        if (body instanceof RestRespBody) {
            return (RestRespBody) body;
        }

        if (statusCode >= 400) {
            respBody = RestRespBody.error(statusCode, "", "");
            if (body != null) {
                respBody.setData(body);
            }
        } else {
            respBody = RestRespBody.ok(body);
        }
        return respBody;
    }

    private int extractStatusCode(Object body, HttpResponse response) {
        if (body instanceof Response) {
            return ((Response) body).getStatus();
        }
        if (response != null) {
            return response.getStatusCode();
        }
        if (body instanceof Exception) {
            return 500;
        }
        return 200;
    }

}
