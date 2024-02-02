package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Header;
import com.jn.agileway.jwt.JWTs;
import com.jn.langx.util.collection.MapAccessor;

import java.util.LinkedHashMap;
import java.util.Map;

class JoseJwtHeaderAdapter extends MapAccessor implements Header{

    JoseJwtHeaderAdapter(){
        super(new LinkedHashMap<String,Object>());
    }

    JoseJwtHeaderAdapter(com.nimbusds.jose.Header delegate){
        super(delegate.getCustomParams());
    }

    @Override
    public Map<String, Object> getAllClaims() {
        return this.getTarget();
    }

    @Override
    public String getType() {
        return this.getString(JWTs.ClaimNames.Header.TYPE);
    }

    @Override
    public void setType(String type) {
        set(JWTs.ClaimNames.Header.TYPE,type);
    }

    @Override
    public String getAlgorithm() {
        return this.getString(JWTs.ClaimNames.Header.ALGORITHM);
    }

    @Override
    public void setAlgorithm(String algorithm) {
        set(JWTs.ClaimNames.Header.ALGORITHM,algorithm);
    }
}
