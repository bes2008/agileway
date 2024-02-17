package com.jn.agileway.jwt;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Consumer2;

import java.util.Map;

class JWSTokenBuilder implements JWTBuilder<JWSToken,JWSTokenBuilder> {

    Map<String, Object> header = Maps.<String, Object>newHashMap();
    Map<String,Object> payload= Maps.<String, Object>newHashMap();


    public JWSTokenBuilder withType(String type) {
        header.put(JWTs.Headers.TYPE, type);
        return this;
    }

    @Override
    public JWSTokenBuilder withAlgorithm(String algorithm) {
        header.put(JWTs.Headers.ALGORITHM, algorithm);
        return this;
    }

    public JWSTokenBuilder withHeader(Map<String,Object> header){
        Collects.forEach(header, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                JWSTokenBuilder.this.withHeaderClaim(key, value);
            }
        });
        return this;
    }

    @Override
    public JWSTokenBuilder withHeaderClaim(String claimName, Object value) {
        header.put(claimName,value);
        return this;
    }
    public JWSTokenBuilder withPayload(Map<String,Object> payload){
        Collects.forEach(payload, new Consumer2<String, Object>() {
            @Override
            public void accept(String key, Object value) {
                JWSTokenBuilder.this.withPayloadClaim(key, value);
            }
        });
        return this;
    }

    @Override
    public JWSTokenBuilder withPayloadClaim(String claimName, Object value) {
        payload.put(claimName,value);
        return this;
    }

    @Override
    public JWSToken build() {
        if(Objs.isEmpty(header)){
            throw new JWTException("header is empty");
        }
        if(Objs.isEmpty(payload)){
            throw new JWTException("payload is empty");
        }

        // header
        if (!header.containsKey(JWTs.Headers.TYPE)){
            header.put(JWTs.Headers.TYPE,JWTs.JWT_TYPE_DEFAULT);
        }

        return new JWSToken(header, payload);
    }

}
