package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.IllegalJWTException;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.JWTPlainToken;
import com.jn.agileway.jwt.JWTSerializer;
import com.nimbusds.jose.PlainHeader;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;

public class JoseJWTSerializer implements JWTSerializer {
    @Override
    public String serialize(JWT jwt) {
        if (jwt instanceof JWTPlainToken){
            return serializePlainToken((JWTPlainToken) jwt);
        }
        return null;
    }

    private String serializePlainToken(JWTPlainToken tk){
        try {
            PlainHeader h = PlainHeader.parse(tk.getHeader().getAllClaims());
            JWTClaimsSet p = JWTClaimsSet.parse(tk.getPayload().getAllClaims());

            return new PlainJWT(h, p).serialize();
        }catch (Throwable e){
            throw new IllegalJWTException("Illegal jwt token, error:" + e.getMessage());
        }
    }
}
