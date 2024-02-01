package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTParser;

public class JoseJwtParser implements JWTParser {
    @Override
    public JWT parse(String jwtstring) {
        try {
            com.nimbusds.jwt.JWT jwt = com.nimbusds.jwt.JWTParser.parse(jwtstring);
            return new JoseJwtAdapter(jwt);
        }catch (Throwable e){
            throw new RuntimeException(e);
        }
    }
}
