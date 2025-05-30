package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Collection;

public class HttpResponseLoggingInterceptor implements HttpResponseInterceptor {
    private static Logger logger = Loggers.getLogger(HttpResponseLoggingInterceptor.class);

    @Override
    public void intercept(HttpResponse response) {
        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append(response.getMethod().name()).append(" ").append(response.getUri()).append(Strings.CRLF);
            for (String name : response.getHttpHeaders().keySet()) {
                Collection<String> headerValues = response.getHttpHeaders().get(name);
                for (String headerValue : headerValues) {
                    builder.append(name).append(": ").append(headerValue).append(Strings.CRLF);
                }
            }

            logger.debug("<<<<" + Strings.CRLF + builder.toString());
        }
    }
}
