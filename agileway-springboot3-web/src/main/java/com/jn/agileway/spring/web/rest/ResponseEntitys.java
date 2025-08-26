package com.jn.agileway.spring.web.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.util.Preconditions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseEntitys {
    public static <T> ResponseEntity<T> fromRestRespBody(@NonNull RestRespBody<T> restRespBody){
        Preconditions.checkNotNull(restRespBody);
        return new ResponseEntity<T>(restRespBody.getData(), HttpStatus.valueOf(restRespBody.getStatusCode()));
    }
}
