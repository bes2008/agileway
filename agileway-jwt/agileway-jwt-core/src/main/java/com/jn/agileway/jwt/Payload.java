package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.io.Charsets;

import java.util.Map;

public class Payload extends KeyValueSet {
    private String encoded;

    /**
     * 该构造方法通常用于生成
     */
    public Payload(Map<String, Object> payload) {
        super(payload);
    }

    /**
     * 该构造方法通常用于解析
     */
    public Payload(String encoded) {
        this(JSONs.<Map<String, Object>>parse(Base64.decodeBase64ToString(encoded), Map.class));
        this.encoded = encoded;
    }

    public String toBase64UrlEncoded() {
        if (encoded == null) {
            this.encoded = Base64.encodeBase64URLSafeString(JSONs.toJson(getAllClaims()).getBytes(Charsets.UTF_8));
        }
        return this.encoded;
    }

}
