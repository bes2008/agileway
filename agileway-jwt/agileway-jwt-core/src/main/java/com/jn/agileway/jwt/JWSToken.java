package com.jn.agileway.jwt;

import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;

import java.util.Map;

public class JWSToken implements JWT {

    Header header;
    Payload payload;

    private String signature;

    /**
     * 该构造方法通常用于生成
     */
    public JWSToken(Header header, Payload payload) {
        this.header = header;
        this.payload = payload;
    }

    /**
     * 该构造方法通常用于生成
     */
    public JWSToken(Map<String, Object> header, Map<String, Object> payload) {
        this(new Header(header), new Payload(payload));
    }

    /**
     * 该构造方法通常用于解析
     */
    public JWSToken(String headerBase64Url, String payloadBase64Url) {
        this.header = new Header(headerBase64Url);
        this.payload = new Payload(payloadBase64Url);
    }

    @Override
    public Header getHeader() {
        return this.header;
    }

    @Override
    public Payload getPayload() {
        return this.payload;
    }


    public String getSignature() {
        return signature;
    }

    void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isSigned() {
        return Objs.isNotEmpty(this.signature);
    }

    public boolean needSigned() {
        return !Objs.equals(this.getHeader().getAlgorithm(), JWTs.JWT_ALGORITHM_PLAIN);
    }

    @Override
    public String toUtf8UrlEncodedToken() {
        if (needSigned() && !isSigned()) {
            throw new JWTException("unsigned token");
        }
        String token = StringTemplates.formatWithPlaceholder("{}.{}.{}", this.header.toBase64UrlEncoded(), this.payload.toBase64UrlEncoded(), this.signature);
        return token;
    }
}
