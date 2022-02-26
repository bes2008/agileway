package com.jn.agileway.spring.web.rest;

import com.jn.agileway.spring.web.mvc.requestmapping.RequestMappingAccessor;
import com.jn.agileway.spring.web.mvc.requestmapping.RequestMappingAccessorRegistry;
import com.jn.agileway.springboot.web.rest.SpringBootErrorControllers;
import com.jn.agileway.web.rest.AbstractGlobalRestResponseBodyHandler;
import com.jn.agileway.web.rest.GlobalRestHandlers;
import com.jn.agileway.web.rest.RestErrorMessageHandler;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.struct.Pair;
import org.slf4j.Logger;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 该类存在的原因：
 * <pre>
 *     1. Spring支持 ReturnValueResolver ，但是自定义的 ReturnValueResolver在 Rest请求处理后，根本不会执行
 *     2. 只有通过@RestControllerAdvice 或者 @ControllerAdvice 定义的ResponseBodyAdvice 才可能被执行
 *     3. 由于@RestControllerAdvice 或者 @ControllerAdvice 注解中的配置不能满足高度定制化的需求，所以我们需要自定义的配置项
 *     4. 由于 @RestControllerAdvice 或者 @ControllerAdvice 定义类的创建不受我们的控制，我们想要自定义必须掌握控制权
 * </pre>
 */
public class GlobalSpringRestResponseBodyHandler extends AbstractGlobalRestResponseBodyHandler<Method> {
    private static final Logger logger = Loggers.getLogger(GlobalSpringRestResponseBodyHandler.class);
    private RequestMappingAccessorRegistry requestMappingAccessorRegistry;

    public void setRequestMappingAccessorRegistry(RequestMappingAccessorRegistry requestMappingAccessorRegistry) {
        this.requestMappingAccessorRegistry = requestMappingAccessorRegistry;
    }

    @Override
    public void init() {
        super.init();
        logger.info("===[AGILE_WAY-SPRING_GLOBAL_REST_RESPONSE_BODY_HANDLER]=== Initial the global rest response body handler for spring mvc: {}", Reflects.getFQNClassName(GlobalSpringRestResponseBodyHandler.class));
    }


    /**
     * @param request
     * @param response
     * @param actionMethod
     * @param actionReturnValue
     * @return
     */
    @Override
    public RestRespBody handleResponseBody(HttpServletRequest request, HttpServletResponse response, Method actionMethod, Object actionReturnValue) {
        if (actionReturnValue instanceof Resource || actionReturnValue instanceof com.jn.langx.io.resource.Resource) {
            return null;
        }

        if (!isSupportedAction(actionMethod)) {
            request.setAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST, true);
            return null;
        }
        RestRespBody body = convertToRestRespBody(request, response, actionMethod, actionReturnValue);
        return body;
    }

    public boolean isSupportedAction(Method actionMethod) {
        boolean supported = configuration.isAcceptable(actionMethod);
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
            logger.debug("{} is not supported for unified response body handler", actionMethod.toString());
        }
        return supported;
    }

    private RestRespBody convertSpringBootErrorBodyToRestRespBody(HttpServletRequest request, HttpServletResponse response, Map<String, Object> tmp) {
        Integer statusCode = (Integer) tmp.get("status");
        RestRespBody respBody = new RestRespBody();
        respBody.setSuccess(false);
        respBody.setStatusCode(statusCode == null ? 404 : statusCode);
        if (tmp.containsKey("error")) {
            respBody.setErrorCode("HTTP-" + respBody.getStatusCode());
        }
        if (tmp.containsKey("message")) {
            respBody.setErrorMessage((String) tmp.get("message"));
        }
        if (tmp.containsKey("timestamp")) {
            respBody.setTimestamp(((Date) tmp.get("timestamp")).getTime());
        }
        return respBody;
    }

    private RestRespBody convertToRestRespBody(HttpServletRequest request, HttpServletResponse response, Method actionMethod, Object body) {
        int statusCode = -1;
        RestRespBody respBody = null;
        if ((body instanceof Map) && SpringBootErrorControllers.isSpringBootErrorControllerHandlerMethod(actionMethod)) {
            respBody = convertSpringBootErrorBodyToRestRespBody(request, response, (Map<String, Object>) body);
            return respBody;
        }

        statusCode = extractStatusCode(body, response);

        if (body instanceof ResponseEntity) {
            body = ((ResponseEntity) body).getBody();
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

    private int extractStatusCode(Object body, HttpServletResponse response) {
        if (body instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) body;
            return responseEntity.getStatusCodeValue();
        }
        if (response != null) {
            return response.getStatus();
        }
        if (body instanceof Exception) {
            return 500;
        }
        return 200;
    }

    public void setRestErrorMessageHandler(RestErrorMessageHandler restErrorMessageHandler) {
        if (restErrorMessageHandler != null) {
            this.restErrorMessageHandler = restErrorMessageHandler;
        }
    }
}
