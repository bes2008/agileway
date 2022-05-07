package com.jn.agileway.springboot.web.rest.exceptionhandler;

import com.jn.agileway.http.rest.RestActionException;
import com.jn.agileway.http.rest.RestActionExceptions;
import com.jn.agileway.web.rest.AbstractServletRestActionExceptionHandler;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Emptys;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
@RestActionExceptions({
        @RestActionException(HttpMediaTypeNotSupportedException.class)
})
public class HttpMediaTypeNotSupportedExceptionHandler extends AbstractServletRestActionExceptionHandler<String> {
    @Override
    public RestRespBody<String> handle(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        HttpMediaTypeNotSupportedException ex = (HttpMediaTypeNotSupportedException) exception;
        RestRespBody<String> respBody = RestRespBody.error(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "HTTP-415", null);

        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        if (Emptys.isNotEmpty(mediaTypes)) {
            String mediaType = MediaType.toString(mediaTypes);
            response.setHeader("Accept", mediaType);
            respBody.setErrorMessage("接受的media Types：" + mediaType);
        }
        return respBody;
    }
}
