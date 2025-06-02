package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpRequest;
import com.jn.agileway.httpclient.core.MessageHeaderConstants;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.mime.MediaType;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.util.Collection;


public class HttpRequestLoggingInterceptor implements HttpRequestInterceptor {
    private static Logger logger = Loggers.getLogger(HttpRequestLoggingInterceptor.class);

    @Override
    public void intercept(HttpRequest request) {
        if (logger.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append(request.getMethod().name()).append(" ").append(request.getUri()).append(Strings.CRLF).append(Strings.CRLF);
            for (String name : request.getHttpHeaders().keySet()) {
                Collection<String> headerValues = request.getHttpHeaders().get(name);
                for (String headerValue : headerValues) {
                    builder.append(name).append(": ").append(headerValue).append(Strings.CRLF);
                }
            }
            ByteArrayOutputStream payload = (ByteArrayOutputStream) request.getPayload();
            if (payload != null && payload.size() > 0) {
                builder.append(Strings.CRLF);
                MediaType contentType = request.getHttpHeaders().getContentType();
                if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(contentType)) {
                    // ignore it
                } else {
                    builder.append(new String(payload.toByteArray()));
                    builder.append(Strings.CRLF);
                }
            }

            logger.debug(">>>>" + Strings.CRLF + builder.toString());
        }
    }
}
