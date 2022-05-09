package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.agileway.jaxrs.rr.JaxrsHttpRequest;
import com.jn.agileway.jaxrs.rr.JaxrsHttpResponse;
import com.jn.langx.http.rest.RestRespBody;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

@Priority(Priorities.USER + 1000)
public class JaxrsGloablRestResponseInterceptor implements WriterInterceptor, ContainerRequestFilter, ContainerResponseFilter {

    private GlobalRestResponseBodyHandler responseBodyHandler;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // 请求时过滤
        JaxrsHttpRequest jaxrsHttpRequest = new JaxrsHttpRequest(requestContext);
        RRLocal.set(jaxrsHttpRequest, null);
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        HttpRequest req = RRLocal.getRequest();
        HttpResponse resp = RRLocal.getResponse();
        boolean fill = req == null || resp == null;
        if (req == null) {
            req = new JaxrsHttpRequest(requestContext);
        }
        if (resp == null) {
            resp = new JaxrsHttpResponse(responseContext);
        }

        if (fill) {
            RRLocal.set(req, resp);
        }
    }

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        System.out.println("interceptor");
        RR rr = RRLocal.get();
        JaxrsHttpRequest request = (JaxrsHttpRequest) rr.getRequest();
        JaxrsHttpResponse response = (JaxrsHttpResponse) rr.getResponse();

        if (responseBodyHandler == null) {
            context.proceed();
        } else {
            Boolean isNotSupportedRestRequest = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST);
            if (isNotSupportedRestRequest != null && !isNotSupportedRestRequest) {
                RestRespBody respBody = responseBodyHandler.handle(request, response, context, context.getEntity());
                context.setEntity(respBody);
            }
            context.proceed();
        }
    }
}
