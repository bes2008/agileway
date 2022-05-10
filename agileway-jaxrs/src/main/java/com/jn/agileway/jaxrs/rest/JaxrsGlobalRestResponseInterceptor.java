package com.jn.agileway.jaxrs.rest;

import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.RR;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.agileway.jaxrs.rr.JaxrsHttpRequest;
import com.jn.agileway.jaxrs.rr.JaxrsHttpResponse;
import com.jn.easyjson.core.JSON;
import com.jn.langx.http.rest.RestRespBody;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;

@Priority(Priorities.USER + 1000)
public class JaxrsGlobalRestResponseInterceptor implements WriterInterceptor, ContainerRequestFilter, ContainerResponseFilter {

    private GlobalRestResponseBodyHandler responseBodyHandler;

    public void setResponseBodyHandler(GlobalRestResponseBodyHandler responseBodyHandler) {
        this.responseBodyHandler = responseBodyHandler;
    }

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
        RR rr = RRLocal.get();
        JaxrsHttpRequest request = (JaxrsHttpRequest) rr.getRequest();
        JaxrsHttpResponse response = (JaxrsHttpResponse) rr.getResponse();

        if (responseBodyHandler == null) {
            context.proceed();
        } else {
            Boolean isNotSupportedRestRequest = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_NON_REST_REQUEST);
            if (isNotSupportedRestRequest != null && !isNotSupportedRestRequest) {
                Object actionMethod = request.getAttribute(GlobalRestHandlers.GLOBAL_REST_ACTION_METHOD);
                RestRespBody respBody = responseBodyHandler.handle(request, response, actionMethod, context.getEntity());

                request.setAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);

                Object finalBody = responseBodyHandler.toMap(request, response, actionMethod, respBody);

                JSON jsons = responseBodyHandler.getContext().getJsonFactory().get();
                finalBody = jsons.toJson(finalBody);
                context.setType(String.class);
                context.setEntity(finalBody);
                context.setMediaType(MediaType.APPLICATION_JSON_TYPE);
            }
            context.proceed();
        }
    }
}
