package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.web.rest.AbstractGlobalServletRestResponseBodyHandler;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.net.http.HttpStatus;

import jakarta.servlet.http.HttpServletResponse;

import java.lang.reflect.Method;

public class GlobalFilterRestResponseHandler extends AbstractGlobalServletRestResponseBodyHandler<Method> {

    @Override
    public RestRespBody handle(HttpRequest request, HttpResponse response, Method method, Object actionReturnValue) {
        // 是否是非rest请求，或者禁用了全局Rest处理的请求，都视为是 非Rest请求
        Boolean nonRestRequest = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST);
        if (nonRestRequest != null && nonRestRequest) {
            return null;
        }

        int statusCode = response.getStatusCode();
        HttpServletResponse servletResponse = (HttpServletResponse) response.getContainerResponse();
        long contentLength = Servlets.getContentLength(servletResponse);
        // 这个==0的判断其实没啥用
        if (contentLength == 0) {
            boolean error = HttpStatus.is4xxClientError(statusCode) || HttpStatus.is5xxServerError(statusCode);
            if (error) {
                //rest response body 是否已写过
                Boolean responseBodyWritten = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN);
                if ((responseBodyWritten == null || !responseBodyWritten) && !servletResponse.isCommitted()) {
                    RestRespBody respBody = new RestRespBody(false, statusCode, "", context.getDefaultRestErrorMessageHandler().getDefaultErrorCode(), context.getDefaultRestErrorMessageHandler().getDefaultErrorMessage());

                    context.getRestErrorMessageHandler().handler(request.getLocale(), respBody);
                    context.getDefaultRestErrorMessageHandler().handler(request.getLocale(), respBody);
                    return respBody;
                }
            }
        }
        return null;
    }
}
