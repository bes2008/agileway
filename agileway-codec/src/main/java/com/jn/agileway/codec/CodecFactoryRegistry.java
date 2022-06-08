package com.jn.agileway.codec;

import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.GenericRegistry;

import java.util.Iterator;
import java.util.ServiceLoader;

public class CodecFactoryRegistry extends GenericRegistry<CodecFactory> {
    @Override
    protected void doInit() throws InitializationException {
        Iterator<CodecFactory> factoryIterator = ServiceLoader.load(CodecFactory.class).iterator();
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
