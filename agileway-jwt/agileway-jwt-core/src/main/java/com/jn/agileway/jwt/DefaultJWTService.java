package com.jn.agileway.jwt;

import com.jn.agileway.jwt.jwe.JWEPlugin;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.spi.CommonServiceProvider;
import com.jn.langx.util.struct.Holder;

public class DefaultJWTService extends AbstractJWTService {
    private Holder<JWEPlugin> jwePluginHolder;

    public DefaultJWTService(){
        initJWEPlugin();
    }

    private void initJWEPlugin(){
        Holder<JWEPlugin> holder = new Holder<JWEPlugin>();
        JWEPlugin plugin = Pipeline.<JWEPlugin>of(new CommonServiceProvider<JWEPlugin>().get(JWEPlugin.class)).findFirst();
        holder.set(plugin);
        this.jwePluginHolder = holder;
    }

    @Override
    public JWEPlugin getJWEPlugin() {
        return jwePluginHolder.get();
    }
}
