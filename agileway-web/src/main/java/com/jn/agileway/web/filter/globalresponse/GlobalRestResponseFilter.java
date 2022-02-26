package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.filter.OncePerRequestFilter;
import com.jn.agileway.web.rest.GlobalRestHandlers;
import com.jn.agileway.web.security.WAFs;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandlerConfiguration;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.type.Types;
import org.slf4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

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
        GlobalFilterRestResponseHandler handler = new GlobalFilterRestResponseHandler();
        GlobalRestResponseBodyHandlerConfiguration handlerConfiguration = new GlobalRestResponseBodyHandlerConfiguration();
        handlerConfiguration.addAssignableType(GlobalRestResponseFilter.class);
        String ignoredFieldsString = filterConfig.getInitParameter("ignoredFields");
        boolean notIgnoreFields = false;
        if (Strings.isNotBlank(ignoredFieldsString)) {
            String[] ignoredFields = Strings.split(ignoredFieldsString, ",");
            if (!Objs.isEmpty(ignoredFields)) {
                handlerConfiguration.setIgnoredFields(Collects.newHashSet(ignoredFields));
            } else {
                notIgnoreFields = true;
            }
        } else {
            if (ignoredFieldsString != null) {
                notIgnoreFields = true;
            }
        }
        // 存在该 配置项，但值为 空
        if (notIgnoreFields) {
            handlerConfiguration.setIgnoredFields(Collects.<String>immutableSet());
        }
        handler.setConfiguration(handlerConfiguration);
        setRestResponseBodyHandler(handler);
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
            try {
                chain.doFilter(request, response);
            } catch (Exception ex) {
                if (exceptionHandler != null) {
                    restRespBody = exceptionHandler.handle(req, resp, doFilterMethod, ex);
                }
            } finally {
                //rest response body 是否已写过
                Boolean responseBodyWritten = (Boolean) request.getAttribute(GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN);
                if ((responseBodyWritten == null || !responseBodyWritten) && !response.isCommitted()) {
                    if (restResponseBodyHandler != null) {
                        restRespBody = restResponseBodyHandler.handleResponseBody(req, resp, doFilterMethod, restRespBody);
                    }

                    if (restRespBody != null && restRespBody.getData() != null) {
                        Object data = restRespBody.getData();
                        Class dataClass = data.getClass();
                        if (!Types.isPrimitive(data.getClass()) && Date.class != dataClass && dataClass != Calendar.class) {
                            restRespBody.setData(WAFs.clearIfContainsJavaScript((String) restRespBody.getData()));
                        }
                    }


                    response.resetBuffer();
                    String json =
                            Servlets.writeToResponse(response, GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8, json);
                }

            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public static final Method doFilterMethod = Reflects.getPublicMethod(GlobalRestResponseFilter.class, "doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
}
