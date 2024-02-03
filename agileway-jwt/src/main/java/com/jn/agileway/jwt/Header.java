package com.jn.agileway.jwt;

import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.collection.Maps;

import java.util.Map;
import java.util.Set;

public class Header extends MapAccessor implements ClaimSet {

    public Header(Map<String, Object> claims){
        super(claims == null ? Maps.newHashMap():claims);
    }

    public Map<String, Object> getAllClaims() {
        return this.getTarget();
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


}
