package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWEToken;
import com.jn.agileway.jwt.Payload;
import com.nimbusds.jwt.EncryptedJWT;

public class JoseJwtEncryptedToken implements JWEToken {
    private EncryptedJWT delegate;
    public JoseJwtEncryptedToken(){

    }

    public JoseJwtEncryptedToken(EncryptedJWT delegate){
        this.delegate = delegate;
    }

    @Override
    public Header getHeader() {
        return null;
    }


    @Override
    public Payload getPayload() {
        return null;
    }


    @Override
    public String toToken() {
        return null;
    }
}
