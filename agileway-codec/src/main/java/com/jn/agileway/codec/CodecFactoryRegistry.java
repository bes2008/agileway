package com.jn.agileway.codec;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.spi.CommonServiceProvider;

import java.util.Iterator;

@Singleton
public class CodecFactoryRegistry extends GenericRegistry<CodecFactory> {
    private static CodecFactoryRegistry INSTANCE;

    private CodecFactoryRegistry() {
    }

    public static CodecFactoryRegistry getInstance() {
        if (INSTANCE == null) {
            synchronized (CodecFactoryRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CodecFactoryRegistry();
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    protected void doInit() throws InitializationException {
        Iterator<CodecFactory> factoryIterator = CommonServiceProvider.loadService(CodecFactory.class).iterator();
        while (factoryIterator.hasNext()) {
            try {
                CodecFactory codecFactory = factoryIterator.next();
                register(codecFactory);
            } catch (Throwable ex) {
                // ignore it
            }
        }
    }
}
