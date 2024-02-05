package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEPlugin;

public class DefaultJWTService extends AbstractJWTService {
    private JWEPlugin jwePlugin;
    @Override
    public JWEPlugin getJWEPlugin() {
        return jwePlugin;
    }
}
