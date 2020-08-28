package com.jn.agileway.spring.web.rest;

import com.jn.langx.http.rest.RestRespBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
public class GlobalSpringRestResponseBodyAdvice implements ResponseBodyAdvice {

    private GlobalSpringRestResponseBodyHandler responseBodyHandler;

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

        RestRespBody respBody = responseBodyHandler.handleResponseBody(httpServletRequest, httpServletResponse, returnType.getMethod(), body);
        if (selectedConverterType == StringHttpMessageConverter.class) {
            return responseBodyHandler.getJsonFactory().get().toJson(respBody);
        }
        return respBody;
    }

    private Object convertToRestRespBody(Object body,
                                         Class selectedConverterType,
                                         ServerHttpRequest request,
                                         ServerHttpResponse response) {
        int statusCode = extractStatusCode(body, response);
        Object originalReturnValue = body;
        if (body instanceof Resource) {
            return body;
        }

        if (body instanceof ResponseEntity) {
            body = ((ResponseEntity) body).getBody();
        }

        if (body instanceof RestRespBody) {
            return body;
        }
        if (body instanceof Resource) {
            return body;
        }

        RestRespBody respBody = null;
        if (statusCode >= 400) {
            respBody = RestRespBody.error(statusCode, "", "");
        } else {
            respBody = RestRespBody.ok(body);
        }

        return respBody;
    }

    private int extractStatusCode(Object body, ServerHttpResponse response) {
        if (body instanceof ResponseEntity) {
            ResponseEntity responseEntity = (ResponseEntity) body;
            return responseEntity.getStatusCodeValue();
        }
        if (response != null) {
            if (response instanceof ServletServerHttpResponse) {
                HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
                return resp.getStatus();
            }
        }
        return 200;
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


    @Autowired
    public void setResponseBodyHandler(GlobalSpringRestResponseBodyHandler responseBodyHandler) {
        this.responseBodyHandler = responseBodyHandler;
    }
}
