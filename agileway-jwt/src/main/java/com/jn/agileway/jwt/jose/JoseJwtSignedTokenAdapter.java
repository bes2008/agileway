package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWSToken;
import com.jn.agileway.jwt.Payload;

public class JoseJwtSignedTokenAdapter implements JWSToken {
    @Override
    public String getBase64UrlEncodedSignature() {
        return null;
    }

    @Override
    public Header getHeader() {
        return null;
    }

    @Override
    public void setHeader(Header header) {

    }

    @Override
    public Payload getPayload() {
        return null;
    }

    @Override
    public void setPayload(Payload payload) {

    }

    @Override
    public String toToken() {
        return null;
    }
}
