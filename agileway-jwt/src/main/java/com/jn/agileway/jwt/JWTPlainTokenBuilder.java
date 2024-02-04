package com.jn.agileway.jwt;

import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Maps;

import java.util.Map;

public class JWTPlainTokenBuilder implements JWTBuilder<JWTPlainToken,JWTPlainTokenBuilder> {

    Map<String, Object> header = Maps.<String, Object>newHashMap();
    Map<String,Object> payload= Maps.<String, Object>newHashMap();

    public JWTPlainTokenBuilder withType(String type) {
        header.put(JWTs.ClaimNames.Header.TYPE, type);
        return this;
    }

    @Override
    public JWTPlainTokenBuilder withAlgorithm(String algorithm) {
        header.put(JWTs.ClaimNames.Header.ALGORITHM, algorithm);
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
        if (!header.containsKey(JWTs.ClaimNames.Header.TYPE)){
            header.put(JWTs.ClaimNames.Header.TYPE,JWTs.JWT_TYPE_DEFAULT);
        }
        header.put(JWTs.ClaimNames.Header.ALGORITHM, JWTs.JWT_ALGORITHM_PLAIN);

        return new JWTPlainToken(header, payload);
    }
}
