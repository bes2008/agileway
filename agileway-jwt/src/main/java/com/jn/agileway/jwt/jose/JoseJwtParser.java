package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTParser;
import com.jn.agileway.jwt.JWTPlainToken;
import com.jn.langx.text.StringTemplates;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.PlainJWT;
import com.nimbusds.jwt.SignedJWT;

public class JoseJwtParser implements JWTParser {
    @Override
    public JWT parse(String jwtstring) {
        try {
            com.nimbusds.jwt.JWT jwt = com.nimbusds.jwt.JWTParser.parse(jwtstring);
            Algorithm algorithm = jwt.getHeader().getAlgorithm();
            if (algorithm==Algorithm.NONE){
                PlainJWT t=(PlainJWT) jwt;
                return new JWTPlainToken(t.getHeader().toJSONObject(), t.getPayload().toJSONObject());
            }
            else if (algorithm instanceof JWSAlgorithm){
                SignedJWT t= (SignedJWT)jwt;
                return new JWSToken(t.getHeader().toJSONObject(), t.getPayload().toJSONObject());
            }else if (algorithm instanceof JWEAlgorithm){
                return new JoseJwtEncryptedToken((EncryptedJWT) jwt);
            }
            else {
                throw new RuntimeException(StringTemplates.formatWithPlaceholder("algorithm unsupported, algorithm: {}, jwt token: {}",algorithm,jwtstring));
            }
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}
