package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Payload;
import com.nimbusds.jwt.SignedJWT;

public class JoseJwtSignedToken implements JWSToken {
    private SignedJWT delegate;
    public JoseJwtSignedToken(){

    }

    public JoseJwtSignedToken(SignedJWT delegate){
        this.delegate= delegate;
    }
    @Override
    public String getBase64UrlEncodedSignature() {
        return null;
    }

    @Override
    public Header getHeader() {
        return null;
    }


    @Override
    public Payload getPayload() {
        return null;
    }

}
