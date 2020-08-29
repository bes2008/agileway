package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.rest.GlobalRestExceptionHandler;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandlerConfiguration;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 配置 该 filter的url pattern时，只能配置在那些 restful api上，不然会出现意想不到的彩蛋
 */
public class GlobalRestResponseFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalRestResponseFilter.class);

    private GlobalRestExceptionHandler exceptionHandler;
    private GlobalRestResponseBodyHandler<Method> restResponseBodyHandler;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        GlobalFilterRestResponseHandler handler = new GlobalFilterRestResponseHandler();
        GlobalRestResponseBodyHandlerConfiguration handlerConfiguration = new GlobalRestResponseBodyHandlerConfiguration();
        handlerConfiguration.addAssignableType(GlobalRestResponseFilter.class);
        handler.setConfiguration(handlerConfiguration);
        setRestResponseBodyHandler(handler);
    }

    public void setExceptionHandler(GlobalRestExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setRestResponseBodyHandler(GlobalFilterRestResponseHandler restResponseBodyHandler) {
        this.restResponseBodyHandler = restResponseBodyHandler;
    }



    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            RestRespBody restRespBody = null;
            try {
                chain.doFilter(request, response);
            } catch (Throwable ex) {
                if (exceptionHandler != null) {
                    restRespBody = exceptionHandler.handleException(req, resp, null, null);
                }
            } finally {
                if (restResponseBodyHandler != null) {
                    restResponseBodyHandler.handleResponseBody(req, resp, doFilterMethod, restRespBody);
                }
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }

    private static final Method doFilterMethod = Reflects.getPublicMethod(GlobalRestResponseFilter.class, "doFilter", ServletRequest.class, ServletResponse.class, FilterChain.class);
}
