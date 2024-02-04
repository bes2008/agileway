package com.jn.agileway.jwt;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.io.Charsets;

import java.util.Map;

public class Payload extends ClaimSet{
    public Payload(Map<String,Object> payload){
        super(payload);
    }

    public String toBase64UrlEncoded(){
        return Base64.encodeBase64URLSafeString(JSONs.toJson(getAllClaims()).getBytes(Charsets.UTF_8));
    }

}
