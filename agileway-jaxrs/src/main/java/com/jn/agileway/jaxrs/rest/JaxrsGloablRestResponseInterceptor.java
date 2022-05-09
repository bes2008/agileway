package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.agileway.jaxrs.rr.JaxrsHttpRequest;
import com.jn.agileway.jaxrs.rr.JaxrsHttpResponse;

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
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        // 响应时过滤
        RR rr = RRLocal.get();
        responseContext.getMediaType();

        JaxrsHttpRequest jaxrsHttpRequest = new JaxrsHttpRequest(requestContext);
        JaxrsHttpResponse jaxrsHttpResponse = new JaxrsHttpResponse(responseContext);
        if (rr == null) {
            RRLocal.set(jaxrsHttpRequest, jaxrsHttpResponse);
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
            responseBodyHandler.handle(request, response, context, context.getEntity());
            context.proceed();
        }
    }
}
