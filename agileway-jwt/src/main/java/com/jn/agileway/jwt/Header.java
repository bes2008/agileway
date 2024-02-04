package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.io.Charsets;

import java.util.Map;
import java.util.Set;

public class Header extends ClaimSet {
    public Header(Map<String, Object> claims){
       super(claims);
    }

    public String getType() {
        return this.getString(JWTs.ClaimNames.Header.TYPE);
    }

    public String getAlgorithm() {
        return this.getString(JWTs.ClaimNames.Header.ALGORITHM);
    }

    public String getContentType() {
        return this.getString(JWTs.ClaimNames.Header.CONTENT_TYPE);
    }

    public Set<String> getCritical() {
        return (Set<String>) this.get(JWTs.ClaimNames.Header.CRITICAL);
    }

    public String toBase64UrlEncoded(){
        return Base64.encodeBase64URLSafeString(JSONs.toJson(getAllClaims()).getBytes(Charsets.UTF_8));
    }

}
