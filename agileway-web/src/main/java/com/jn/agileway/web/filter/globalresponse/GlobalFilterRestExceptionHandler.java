package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.web.rest.GlobalRestExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在 javax.servlet.Filter 里处理全局异常
 */
public class GlobalFilterRestExceptionHandler extends GlobalRestExceptionHandler {
    @Override
    protected boolean isSupportedRestAction(HttpServletRequest request, HttpServletResponse response, Object action, Exception ex) {
        return action == GlobalRestResponseFilter.doFilterMethod;
    }
}
