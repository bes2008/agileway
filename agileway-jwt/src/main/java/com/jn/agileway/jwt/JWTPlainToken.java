package com.jn.agileway.jwt;

import java.util.Map;

public class JWTPlainToken implements JWT{
    Header header;
    Payload payload;

    public JWTPlainToken(Header header, Payload payload){
        this.header = header;
        this.payload = payload;
    }

    public JWTPlainToken(Map<String,Object> header, Map<String, Object> payload){
        this(new Header(header), new Payload(payload));
    }

    @Override
    public Header getHeader() {
        return this.header;
    }

    @Override
    public Payload getPayload() {
        return this.payload;
    }
}
