package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.io.Charsets;

import java.util.Map;
import java.util.Set;

public class Header extends ClaimSet {
    private String encoded;

    /**
     * 该构造方法通常用于生成
     */
    public Header(Map<String, Object> claims) {
        super(claims);
    }

    /**
     * 该构造方法通常用于解析
     */
    public Header(String encoded) {
        this(JSONs.<Map<String, Object>>parse(Base64.decodeBase64ToString(encoded), Map.class));
        this.encoded = encoded;
    }

    public String getType() {
        return this.getString(JWTs.Headers.TYPE);
    }

    public String getAlgorithm() {
        return this.getString(JWTs.Headers.ALGORITHM);
    }

    public String getContentType() {
        return this.getString(JWTs.Headers.CONTENT_TYPE);
    }

    public Set<String> getCritical() {
        return (Set<String>) this.get(JWTs.Headers.CRITICAL);
    }

    public String toBase64UrlEncoded() {
        if (encoded == null) {
            this.encoded = Base64.encodeBase64URLSafeString(JSONs.toJson(getAllClaims()).getBytes(Charsets.UTF_8));
        }
        return encoded;
    }

}
