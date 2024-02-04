package com.jn.agileway.jwt;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;

import java.util.Map;

public class JWSToken extends JWTPlainToken {
    public JWSToken(Header header, Payload payload) {
        super(header, payload);
    }

    public JWSToken(Map<String, Object> header, Map<String, Object> payload) {
        super(header, payload);
    }

    private String signature;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isSigned(){
        return Objs.isNotEmpty(this.signature);
    }

    @Override
    public String toUtf8UrlEncodedToken() {
        if(!isSigned()){
            throw new IllegalJWTException("unsigned token");
        }
        String token= StringTemplates.formatWithPlaceholder("{}.{}.{}", this.header.toBase64UrlEncoded(), this.payload.toBase64UrlEncoded(), this.signature);
        return token;
    }
}
