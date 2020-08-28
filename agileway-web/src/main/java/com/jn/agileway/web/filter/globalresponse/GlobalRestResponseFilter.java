package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.rest.GlobalRestExceptionHandler;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandler;
import com.jn.agileway.web.servlet.Servlets;
import com.jn.langx.http.HttpHeaders;
import com.jn.langx.http.HttpStatus;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GlobalRestResponseFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalRestResponseFilter.class);

    private GlobalRestExceptionHandler exceptionHandler;
    private GlobalRestResponseBodyHandler restResponseBodyHandler;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void setExceptionHandler(GlobalRestExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    public void setRestResponseBodyHandler(GlobalRestResponseBodyHandler restResponseBodyHandler) {
        this.restResponseBodyHandler = restResponseBodyHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request instanceof HttpServletRequest){
            HttpServletRequest req = (HttpServletRequest)request;
            HttpServletResponse resp = (HttpServletResponse)response;
            try{
                chain.doFilter(request, response);
            }catch (Throwable ex){
                if(exceptionHandler!=null){
                    exceptionHandler.handleException(req,resp,null,null);
                }
            }finally {
                String contentLengthStr = resp.getHeader(HttpHeaders.CONTENT_LENGTH);
                int statusCode = resp.getStatus();
                contentLengthStr= Strings.useValueIfEmpty(contentLengthStr,"0");
                long contentLength = Servlets.getContentLength(resp);
                if(contentLength==0){
                    boolean error = HttpStatus.is4xxClientError(statusCode) || HttpStatus.is5xxServerError(statusCode);
                    RestRespBody respBody = new RestRespBody(!error, statusCode, "",null,null);
                }
            }
        }else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
