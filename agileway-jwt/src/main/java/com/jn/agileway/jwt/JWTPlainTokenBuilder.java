package com.jn.agileway.jwt;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Maps;

import java.util.Map;

public class JWTPlainTokenBuilder implements JWTBuilder<JWTPlainToken,JWTPlainTokenBuilder> {

    Map<String, Object> header = Maps.<String, Object>newHashMap();
    Map<String,Object> payload= Maps.<String, Object>newHashMap();

    public JWTPlainTokenBuilder withType(String type) {
        header.put(JWTs.Headers.TYPE, type);
        return this;
    }

    @Override
    public JWTPlainTokenBuilder withAlgorithm(String algorithm) {
        header.put(JWTs.Headers.ALGORITHM, algorithm);
        return this;
    }

    @Override
    public JWTPlainTokenBuilder withHeaderClaim(String claimName, Object value) {
        header.put(claimName,value);
        return this;
    }

    @Override
    public JWTPlainTokenBuilder withPayloadClaim(String claimName, Object value) {
        payload.put(claimName,value);
        return this;
    }

    @Override
    public JWTPlainToken build() {
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
        header.put(JWTs.Headers.ALGORITHM, JWTs.JWT_ALGORITHM_PLAIN);

        return new JWTPlainToken(header, payload);
    }
}
