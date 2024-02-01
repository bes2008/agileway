package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWT;
import com.jn.agileway.jwt.Payload;

import java.util.List;

public class JoseJwtAdapter implements JWT {
    com.nimbusds.jwt.JWT delegate;
    public JoseJwtAdapter(){

    }

    JoseJwtAdapter(com.nimbusds.jwt.JWT delegate){
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
    public String getJWTString() {
        return null;
    }

    @Override
    public List<String> getParts() {
        return null;
    }
}
