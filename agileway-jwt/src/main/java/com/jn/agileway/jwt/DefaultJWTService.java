package com.jn.agileway.jwt;

import com.jn.agileway.jwt.sign.Signs;
import java.util.List;

public class DefaultJWTService extends AbstractJWTService {
    private JWEPlugin jwePlugin;
    @Override
    public JWEPlugin getJWEPlugin() {
        return jwePlugin;
    }
}
