package com.jn.agileway.jwt;

import com.jn.langx.text.StringTemplates;

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

    @Override
    public String toUtf8UrlEncodedToken() {
        return StringTemplates.formatWithPlaceholder("{}.{}", this.header.toBase64UrlEncoded(), this.payload.toBase64UrlEncoded());
    }
}
