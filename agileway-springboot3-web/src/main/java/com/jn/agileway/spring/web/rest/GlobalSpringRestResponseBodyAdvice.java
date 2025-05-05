package com.jn.agileway.spring.web.rest;

import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.web.servlet.ServletHttpRequestFactory;
import com.jn.agileway.web.servlet.ServletHttpResponseFactory;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestControllerAdvice
public class GlobalSpringRestResponseBodyAdvice implements ResponseBodyAdvice, InitializingBean {
    private static final Logger logger = Loggers.getLogger(GlobalSpringRestResponseBodyAdvice.class);
    private GlobalSpringRestResponseBodyHandler responseBodyHandler;

    public GlobalSpringRestResponseBodyAdvice() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("===[AGILE_WAY-SPRING_GLOBAL_REST_RESPONSE_ADVICE]=== Initial the spring global rest response body advice: {}", Reflects.getFQNClassName(getClass()));
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return responseBodyHandler.isSupportedAction(returnType.getMethod());
    }


    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        HttpServletRequest httpServletRequest = extractHttpServletRequest(request);
        HttpServletResponse httpServletResponse = extractHttpServletResponse(response);
        if (httpServletRequest == null || httpServletResponse == null) {
            return body;
        }

        HttpRequest req = ServletHttpRequestFactory.INSTANCE.get(httpServletRequest);
        HttpResponse resp = ServletHttpResponseFactory.INSTANCE.get(httpServletResponse);

        final RestRespBody respBody = responseBodyHandler.handle(req, resp, returnType.getMethod(), body);
        if (respBody == null) {
            return null;
        }
        httpServletResponse.setStatus(respBody.getStatusCode());
        httpServletResponse.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
        httpServletResponse.setCharacterEncoding(Charsets.UTF_8.name());
        httpServletRequest.setAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);
        Map<String, Object> finalBody = responseBodyHandler.toMap(req, resp, returnType.getMethod(), respBody);

        if (selectedConverterType == StringHttpMessageConverter.class) {
            String json = responseBodyHandler.getContext().getJsonFactory().get().toJson(finalBody);
            return json;
        }

        return finalBody;
    }


    private HttpServletRequest extractHttpServletRequest(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            return ((ServletServerHttpRequest) request).getServletRequest();
        }
        return null;
    }

    private HttpServletResponse extractHttpServletResponse(ServerHttpResponse response) {
        if (response instanceof ServletServerHttpResponse) {
            return ((ServletServerHttpResponse) response).getServletResponse();
        }
        return null;
    }


    @Autowired(required = false)
    public void setResponseBodyHandler(GlobalSpringRestResponseBodyHandler responseBodyHandler) {
        this.responseBodyHandler = responseBodyHandler;
    }

}
