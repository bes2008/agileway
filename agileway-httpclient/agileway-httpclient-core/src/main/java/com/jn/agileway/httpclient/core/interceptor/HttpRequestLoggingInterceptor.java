package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Collection;


public class HttpRequestLoggingInterceptor implements HttpRequestInterceptor {
    private static Logger logger = Loggers.getLogger(HttpRequestLoggingInterceptor.class);

    @Override
    public void intercept(HttpRequest request) {
        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append(request.getMethod().name()).append(" ").append(request.getUri()).append(Strings.CRLF);
            for (String name : request.getHeaders().keySet()) {
                Collection<String> headerValues = request.getHeaders().get(name);
                for (String headerValue : headerValues) {
                    builder.append(name).append(": ").append(headerValue).append(Strings.CRLF);
                }
            }

            logger.debug(">>>>" + Strings.CRLF + builder.toString());
        }
    }
}
