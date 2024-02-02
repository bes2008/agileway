package com.jn.agileway.jwt;


import com.jn.agileway.jwt.spi.JWTService;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.spi.CommonServiceProvider;

public class JWTServiceProvider {
    public static JWTService getJWTService(){
        return Pipeline.<JWTService>of(new CommonServiceProvider<JWTService>().get(JWTService.class))
                .findFirst();
    }
}
