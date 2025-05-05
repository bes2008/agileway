package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.servlet.ServletHttpRequestFactory;
import com.jn.agileway.web.servlet.ServletHttpResponseFactory;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * 配置 该 filter的url pattern时，只能配置在那些 restful api上，不然会出现意想不到的彩蛋
 */
public class GlobalRestResponseFilter extends OncePerRequestFilter {
    private static final Logger logger = Loggers.getLogger(GlobalRestResponseFilter.class);

    private GlobalFilterRestExceptionHandler exceptionHandler;
    private GlobalFilterRestResponseHandler restResponseBodyHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        logger.info("Initial the global rest response filter");
    }

    public void setExceptionHandler(GlobalFilterRestExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setRestResponseBodyHandler(GlobalFilterRestResponseHandler restResponseBodyHandler) {
        this.restResponseBodyHandler = restResponseBodyHandler;
    }

    @Override
    public void doFilterInternal(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            RestRespBody restRespBody = null;

            HttpRequest httpRequest = ServletHttpRequestFactory.INSTANCE.get(req);
            HttpResponse httpResponse = ServletHttpResponseFactory.INSTANCE.get(resp);
            try {
                chain.doFilter(request, response);
            } catch (Exception ex) {
                if (exceptionHandler != null) {
                    restRespBody = exceptionHandler.handle(httpRequest, httpResponse, doFilterMethod, ex);
                }
            } finally {
                //rest response body 是否已写过
                Boolean responseBodyWritten = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN);
                if (restResponseBodyHandler != null && ((responseBodyWritten == null || !responseBodyWritten) && !response.isCommitted())) {
                    restRespBody = restResponseBodyHandler.handle(httpRequest, httpResponse, doFilterMethod, restRespBody);
                    if (restRespBody != null) {
                        Map<String, Object> finalBody = restResponseBodyHandler.toMap(httpRequest, httpResponse, doFilterMethod, restRespBody);

                        resp.setStatus(restRespBody.getStatusCode());
                        response.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
                        response.setCharacterEncoding(Charsets.UTF_8.name());
                        request.setAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);

                        response.resetBuffer();
                        String json = restResponseBodyHandler.getContext().getJsonFactory().get().toJson(finalBody);
                        Servlets.writeToResponse(resp, GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8, json);
                    }
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public static final Method doFilterMethod = Reflects.getPublicMethod(GlobalRestResponseFilter.class, "doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
}
