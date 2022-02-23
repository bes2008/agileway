package com.jn.agileway.springboot.web.rest;

import com.jn.agileway.web.rest.GlobalRestHandlers;
import com.jn.agileway.web.rest.GlobalRestResponseBodyHandlerConfiguration;
import com.jn.agileway.web.servlet.Servlets;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AgilewayDefaultErrorAttributes extends DefaultErrorAttributes {
    private GlobalRestResponseBodyHandlerConfiguration configuration;

    public AgilewayDefaultErrorAttributes() {
    }

    public AgilewayDefaultErrorAttributes(GlobalRestResponseBodyHandlerConfiguration configuration) {
        setConfiguration(configuration);
    }

    public GlobalRestResponseBodyHandlerConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(GlobalRestResponseBodyHandlerConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            Map<String, Object> tmp = super.getErrorAttributes(servletRequestAttributes, includeStackTrace);
            HttpServletRequest request = servletRequestAttributes.getRequest();
            String acceptHeader = request.getHeader("accept");
            if (acceptHeader != null && acceptHeader.contains("text/html")) {
                return tmp;
            }
            Map<String, Object> respBody = new HashMap<String, Object>();
            Integer statusCode = (Integer) tmp.get("status");
            respBody.put("success", false);
            respBody.put("statusCode", statusCode);
            if (tmp.containsKey("error")) {
                respBody.put("errorCode", (String) tmp.get("error"));
            }
            if (tmp.containsKey("message")) {
                respBody.put("errorMessage", (String) tmp.get("message"));
            }
            if (tmp.containsKey("path") && !configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_URL)) {
                respBody.put("url", (String) tmp.get("path"));
            }
            if (tmp.containsKey("timestamp")) {
                respBody.put("timestamp", ((Date) tmp.get("timestamp")).getTime());
            }
            if (!configuration.isIgnoredField(GlobalRestHandlers.GLOBAL_REST_FIELD_REQUEST_HEADERS)) {
                respBody.put("requestHeaders", Servlets.headersToMultiValueMap(request));
            }
            return respBody;
        } else {
            return super.getErrorAttributes(requestAttributes, includeStackTrace);
        }

    }
}
