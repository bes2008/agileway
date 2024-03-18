package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEPlugin;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.langx.util.struct.Holder;

public class DefaultJWTService extends AbstractJWTService {
    private Holder<JWEPlugin> jwePluginHolder;

    @Override
    public JWEPlugin getJWEPlugin() {
        if (jwePluginHolder == null) {
            synchronized (this) {
                if (jwePluginHolder == null) {
                    jwePluginHolder = new Holder<JWEPlugin>();
                    JWEPlugin plugin = Pipeline.<JWEPlugin>of(new CommonServiceProvider<JWEPlugin>().get(JWEPlugin.class)).findFirst();
                    jwePluginHolder.set(plugin);
                }
            }
        }
        return jwePluginHolder.get();
    }
}
