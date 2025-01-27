package com.jn.agileway.audit.servlet;

import com.jn.agileway.audit.core.AuditRequest;
import com.jn.agileway.audit.core.operation.OperationParametersExtractor;
import com.jn.agileway.web.servlet.HttpServletRequestStreamWrapper;
import com.jn.langx.invocation.MethodInvocation;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.net.mime.MediaType;
import org.slf4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServletHttpParametersExtractor implements OperationParametersExtractor<HttpServletRequest, MethodInvocation> {
    private static final Logger logger = Loggers.getLogger(ServletHttpParametersExtractor.class);


    @Override
    public Map<String, Object> get(AuditRequest<HttpServletRequest, MethodInvocation> wrappedRequest) {
        HttpServletRequest request = wrappedRequest.getRequest();
        Map<String, String[]> parameterMap = request.getParameterMap();
        final Map<String, Object> ret = new LinkedHashMap<String, Object>();
        Collects.forEach(parameterMap, new Consumer2<String, String[]>() {
            @Override
            public void accept(String key, String[] values) {
                if (Objs.length(values) > 1) {
                    ret.put(key, Collects.newArrayList(values));
                } else {
                    ret.put(key, values[0]);
                }
            }
        });

        boolean readBody = true;
        int contentLength = request.getContentLength();
        // 如果 请求头 content-length没有设置的话，该值可能为 -1
        if (contentLength == 0) {
            readBody = false;
        }
        if (!(request instanceof HttpServletRequestStreamWrapper)) {
            readBody = false;
        }
        String contentType = request.getContentType();
        if (readBody && Emptys.isNotEmpty(contentType)) {
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType) || MediaType.MULTIPART_FORM_DATA_VALUE.equals(contentType)) {
                // 在这两种contentType下，上面的 getParameter时 就已经将这部分内容读过了
                readBody = false;
            }
        }

        if(readBody) {
            // 在使用 spring mvc 时，文件上传时，可以不提供 multipart 请求头
            // 从servlet 3.0 开始，有该方法
            Collection<Part> parts = null;
            try {
                parts = request.getParts();
            } catch (Throwable ex) {
                // ignore it
            }
            if (Emptys.isNotEmpty(parts)) {
                readBody = false;
            }
        }

        if (readBody) {
            try {
                HttpServletRequestStreamWrapper requestWrapper = (HttpServletRequestStreamWrapper) request;
                String content = new String(requestWrapper.getRequestBody(), Charsets.UTF_8);
                ret.put("REQUEST_BODY", content);
            } catch (Throwable ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }
        return ret;
    }
}
