package com.jn.agileway.spring.web.rest;

import com.jn.agileway.web.rest.*;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 该类存在的原因：
 * <pre>
 *     1. Spring支持 ReturnValueResolver ，但是自定义的 ReturnValueResolver在 Rest请求处理后，根本不会执行
 *     2. 只有通过@RestControllerAdvice 或者 @ControllerAdvice 定义的ResponseBodyAdvice 才可能被执行
 *     3. 由于@RestControllerAdvice 或者 @ControllerAdvice 注解中的配置不能满足高度定制化的需求，所以我们需要自定义的配置项
 *     4. 由于 @RestControllerAdvice 或者 @ControllerAdvice 定义类的创建不受我们的控制，我们想要自定义必须掌握控制权
 * </pre>
 */
public class GlobalSpringRestResponseBodyHandler implements GlobalRestResponseBodyHandler<Method>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(GlobalSpringRestResponseBodyHandler.class);
    private GlobalRestResponseBodyHandlerConfiguration configuration = new GlobalRestResponseBodyHandlerConfiguration();
    private JSONFactory jsonFactory;
    private RestErrorMessageHandler restErrorMessageHandler = NoopRestErrorMessageHandler.INSTANCE;

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("===[AGILE_WAY-SPRING_GLOBAL_REST_RESPONSE_BODY_HANDLER]=== Initial the global rest response body handler for spring mvc: {}", Reflects.getFQNClassName(GlobalSpringRestResponseBodyHandler.class));
    }

    @Override
    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
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
        if (!isSupportedAction(actionMethod)) {
            request.setAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST, true);
            return null;
        }
        if (actionReturnValue instanceof Resource) {
            return null;
        }
        RestRespBody body = convertToRestRespBody(request, response, actionReturnValue);
        if (body != null && body.getStatusCode() >= 400) {
            restErrorMessageHandler.handler(request.getLocale(), body);
        }
        body.setUrl(request.getRequestURL().toString());
        response.setStatus(body.getStatusCode());
        response.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
        response.setCharacterEncoding(Charsets.UTF_8.name());
        request.setAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);
        body.setMethod(Servlets.getMethod(request));
        body.setRequestHeaders(Servlets.headersToMultiValueMap(request));
        return body;
    }

    public boolean isSupportedAction(Method actionMethod) {
        boolean supported = configuration.isAcceptable(actionMethod);
        if (!supported) {
            logger.debug("{} is not supported for unified response body handler", actionMethod.toString());
        }
        return supported;
    }


    private RestRespBody convertToRestRespBody(HttpServletRequest request, HttpServletResponse response, Object body) {
        int statusCode = extractStatusCode(body, response);

        if (body instanceof ResponseEntity) {
            body = ((ResponseEntity) body).getBody();
        }

        if (body instanceof RestRespBody) {
            return (RestRespBody) body;
        }

        RestRespBody respBody = null;
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

    @Override
    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public JSONFactory getJsonFactory() {
        return jsonFactory;
    }

    public void setRestErrorMessageHandler(RestErrorMessageHandler restErrorMessageHandler) {
        if (restErrorMessageHandler != null) {
            this.restErrorMessageHandler = restErrorMessageHandler;
        }
    }
}
