package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTParser;
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
                return new JoseJwtPlainTokenAdapter((PlainJWT) jwt);
            }
            else if (algorithm instanceof JWSAlgorithm){
                return new JoseJwtSignedTokenAdapter((SignedJWT)jwt);
            }else if (algorithm instanceof JWEAlgorithm){
                return new JoseJwtEncryptedTokenAdapter((EncryptedJWT) jwt);
            }
            else {
                throw new RuntimeException(StringTemplates.formatWithPlaceholder("algorithm unsupported, algorithm: {}, jwt token: {}",algorithm,jwtstring));
            }
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}
