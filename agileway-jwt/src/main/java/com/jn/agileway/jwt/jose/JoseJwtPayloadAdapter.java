package com.jn.agileway.jwt.jose;

import com.jn.agileway.jwt.Payload;
import com.jn.langx.util.collection.MapAccessor;

import java.util.Map;

public class JoseJwtPayloadAdapter extends MapAccessor implements Payload {
    @Override
    public Map<String, Object> getAllClaims() {
        return this.getTarget();
    }
}
