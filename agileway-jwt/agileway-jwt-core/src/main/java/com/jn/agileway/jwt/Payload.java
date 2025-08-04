package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.io.Charsets;

import java.util.Map;
import java.util.Set;

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
            this.encoded = Base64.encodeBase64URLSafeString(JSONs.toJson(getAll()).getBytes(Charsets.UTF_8));
        }
        return this.encoded;
    }

    public Set<String> getClaimNames() {
        return getKeys();
    }

    public Map<String, Object> getAllClaims() {
        return getAll();
    }

    /**
     * 获取Issuer ，颁发者
     */
    public String getIssuer() {
        return getString(JWTs.ClaimKeys.ISSUER);
    }

    /**
     * 令牌的subject，主题，通常是登录用户
     */
    public String getSubject() {
        return getString(JWTs.ClaimKeys.SUBJECT);
    }

    /**
     * 获取令牌接收者，即接收该令牌的用户，或者 resource server
     */
    public String getAudience() {
        return getString(JWTs.ClaimKeys.AUDIENCE);
    }

    /**
     * 获取令牌ID，通常用于标识该令牌
     */
    public String getJwtId() {
        return getString(JWTs.ClaimKeys.JWT_ID);
    }

    /**
     * 获取令牌颁发时间，unix 时间戳 ，单位：s
     */
    public Long getIssuedAt() {
        return getLong(JWTs.ClaimKeys.ISSUED_AT);
    }

    /**
     * 获取令牌开始生效时间，unix 时间戳 ，单位：s
     */
    public Long getNotBefore() {
        return getLong(JWTs.ClaimKeys.NOT_BEFORE);
    }

    /**
     * 获取令牌有效期，unix 时间戳 ，单位：s
     */
    public Long getExpiration() {
        return getLong(JWTs.ClaimKeys.EXPIRATION);
    }
}
