package com.jn.agileway.codec.serialization.kryo.customizer;

import com.esotericsoftware.kryo.Kryo;
import com.jn.agileway.codec.serialization.kryo.KryoCustomizer;
import com.jn.langx.util.ClassLoaders;
import de.javakaffee.kryoserializers.cglib.CGLibProxySerializer;

public class CGLibProxyKryoCustomizer implements KryoCustomizer {
    @Override
    public String getName() {
        return "cglib_proxy";
    }

    @Override
    public void customize(Kryo kryo) {
        if (isCglibFound()) {
            kryo.register(CGLibProxySerializer.CGLibProxyMarker.class, new CGLibProxySerializer());
        }
    }

    private boolean isCglibFound() {
        try {
            Class c = ClassLoaders.loadClass("net.sf.cglib.proxy.Enhancer");
            return c != null;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }
}
