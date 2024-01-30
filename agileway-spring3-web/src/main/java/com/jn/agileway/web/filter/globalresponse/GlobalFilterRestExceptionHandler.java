package com.jn.agileway.web.filter.globalresponse;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.web.rest.AbstractGlobalServletRestExceptionHandler;


/**
 * 在 javax.servlet.Filter 里处理全局异常
 */
public class GlobalFilterRestExceptionHandler extends AbstractGlobalServletRestExceptionHandler {
    @Override
    protected boolean isSupportedRestAction(HttpRequest request, HttpResponse response, Object action, Exception ex) {
        return action == GlobalRestResponseFilter.doFilterMethod;
    }
}
