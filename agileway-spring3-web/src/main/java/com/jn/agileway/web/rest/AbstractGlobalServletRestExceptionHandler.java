package com.jn.agileway.web.rest;

import com.jn.agileway.http.rest.AbstractGlobalRestExceptionHandler;
import com.jn.agileway.http.rest.GlobalRestHandlers;
import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.io.Charsets;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.jn.agileway.http.rest.GlobalRestHandlers.GLOBAL_REST_RESPONSE_HAD_WRITTEN;

public abstract class AbstractGlobalServletRestExceptionHandler extends AbstractGlobalRestExceptionHandler {
    private static final Logger logger = Loggers.getLogger(AbstractGlobalServletRestExceptionHandler.class);
    @Override
    protected void writeResponse(HttpRequest request, HttpResponse response, Object action, RestRespBody respBody) {
        Map<String, Object> finalBody = toMap(request, response, action, respBody);

        try {
            HttpServletResponse servletResponse = (HttpServletResponse)response.getContainerResponse();
            if (!servletResponse.isCommitted()) {
                servletResponse.resetBuffer();
                servletResponse.setStatus(respBody.getStatusCode());
                String jsonstring = context.getJsonFactory().get().toJson(finalBody);

                servletResponse.setContentType(GlobalRestHandlers.RESPONSE_CONTENT_TYPE_JSON_UTF8);
                servletResponse.setCharacterEncoding(Charsets.UTF_8.name());
                servletResponse.getWriter().write(jsonstring);
                request.setAttribute(GLOBAL_REST_RESPONSE_HAD_WRITTEN, true);
            }
        } catch (IOException ioe) {
            logger.warn(ioe.getMessage(), ioe);
        }
    }
}
