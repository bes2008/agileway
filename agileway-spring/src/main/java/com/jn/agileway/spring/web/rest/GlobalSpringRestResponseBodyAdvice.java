package com.jn.agileway.spring.web.rest;

import com.jn.agileway.web.filter.waf.WAFs;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.function.Supplier0;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalSpringRestResponseBodyAdvice implements ResponseBodyAdvice, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(GlobalSpringRestResponseBodyAdvice.class);
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

        final RestRespBody respBody = responseBodyHandler.handleResponseBody(httpServletRequest, httpServletResponse, returnType.getMethod(), body);
        if (selectedConverterType == StringHttpMessageConverter.class) {
            String json = responseBodyHandler.getJsonFactory().get().toJson(respBody);
            String xssFilteredData = WAFs.clearIfContainsJavaScript(json);
            if (Objs.isEmpty(xssFilteredData)) {
                respBody.setData(null);
                json = responseBodyHandler.getJsonFactory().get().toJson(respBody);
            }
            return json;
        }

        String xssFilteredData = WAFs.clearIfContainsJavaScript(new Supplier0<String>() {
            @Override
            public String get() {
                return responseBodyHandler.getJsonFactory().get().toJson(respBody);
            }
        });
        if (Objs.isEmpty(xssFilteredData)) {
            respBody.setData(null);
        }

        return respBody;
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

    public GlobalSpringRestResponseBodyHandler getResponseBodyHandler() {
        return responseBodyHandler;
    }
}
