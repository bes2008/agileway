package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.Payload;
import com.nimbusds.jwt.PlainJWT;

public class JoseJwtPlainTokenAdapter implements JWT {
    PlainJWT delegate;
    public JoseJwtPlainTokenAdapter(){

    }

    public JoseJwtPlainTokenAdapter(PlainJWT delegate){
        this.delegate=delegate;
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
