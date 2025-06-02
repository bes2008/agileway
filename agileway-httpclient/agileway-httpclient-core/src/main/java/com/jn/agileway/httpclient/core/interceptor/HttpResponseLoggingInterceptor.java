package com.jn.agileway.httpclient.core.interceptor;

import com.jn.agileway.httpclient.core.HttpResponse;
import com.jn.agileway.httpclient.util.HttpClientUtils;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.mime.MediaType;
import org.slf4j.Logger;

import java.nio.charset.Charset;
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
            Object payload = response.getPayload();
            if (payload != null && payload.getClass() == byte[].class) {
                MediaType contentType = response.getHttpHeaders().getContentType();
                String contentDispositionValue = response.getHttpHeaders().getFirstHeader("Content-Disposition");
                if (!HttpClientUtils.isAttachmentResponse(contentDispositionValue)) {
                    builder.append(Strings.CRLF);
                    Charset charset = contentType.getCharset();
                    if (charset == null) {
                        charset = Charsets.UTF_8;
                    }
                    builder.append(new String((byte[]) payload, charset));
                    builder.append(Strings.CRLF);
                }

            }
            logger.debug("<<<<" + Strings.CRLF + builder.toString());
        }
    }
}
