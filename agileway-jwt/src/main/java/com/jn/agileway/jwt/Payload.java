package com.jn.agileway.jwt;

import com.jn.langx.util.collection.MapAccessor;

import java.util.Map;

public class Payload extends MapAccessor implements ClaimSet{
    public Payload(Map<String,Object> payload){
        super(payload);
    }

    @Override
    public Map<String, Object> getAllClaims() {
        return this.getTarget();
    }
}
