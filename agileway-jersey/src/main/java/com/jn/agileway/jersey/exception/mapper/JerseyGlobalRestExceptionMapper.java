package com.jn.agileway.jersey.exception.mapper;

import com.jn.agileway.http.rr.HttpRequest;
import com.jn.agileway.http.rr.HttpResponse;
import com.jn.agileway.http.rr.RRLocal;
import com.jn.agileway.jaxrs.rest.JaxrsGlobalRestExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.Charsets;
import org.glassfish.jersey.spi.ExtendedExceptionMapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

public class JerseyGlobalRestExceptionMapper implements ExtendedExceptionMapper<Throwable> {
    private JaxrsGlobalRestExceptionHandler exceptionHandler;

    public void setExceptionHandler(JaxrsGlobalRestExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public boolean isMappable(Throwable exception) {
        return exceptionHandler.getExceptionHandlerRegistry().findExceptionResolver(exception) != null;
    }

    @Override
    public Response toResponse(Throwable exception) {
        HttpRequest request = RRLocal.getRequest();
        HttpResponse response = RRLocal.getResponse();
        Exception e = null;
        if (!(exception instanceof Exception)) {
            e = Throwables.wrapAsRuntimeException(exception);
        } else {
            e = (Exception) exception;
        }
        RestRespBody respBody = exceptionHandler.handle(request, response, null, e);
        Object entity = exceptionHandler.toMap(request, response, null, respBody);
        String responseBody= exceptionHandler.getContext().getJsonFactory().get().toJson(entity);
        return Response.status(respBody.getStatusCode())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .encoding(Charsets.UTF_8.name())
                .entity(responseBody)
                .build();
    }
}
