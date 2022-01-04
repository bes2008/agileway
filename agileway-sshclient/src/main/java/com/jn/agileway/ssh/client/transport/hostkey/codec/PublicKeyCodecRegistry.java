package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.transport.hostkey.keytype.DefaultPublicKeyHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.PublicKeyHostKeyTypeExtractor;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.logging.Loggers;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PublicKeyCodecRegistry extends GenericRegistry<PublicKeyCodec> {
    private static final PublicKeyCodecRegistry INSTANCE = new PublicKeyCodecRegistry();

    private PublicKeyCodecRegistry() {
        super(new ConcurrentHashMap<String, PublicKeyCodec>());
        init();
    }

    @Override
    protected void doInit() throws InitializationException {
        Iterator<PublicKeyCodec> loader = ServiceLoader.load(PublicKeyCodec.class).iterator();
        while (loader.hasNext()) {
            try {
                PublicKeyCodec codec = loader.next();
                register(codec);
                if (codec instanceof PublicKeyHostKeyTypeExtractor) {
                    DefaultPublicKeyHostKeyTypeExtractor.getInstance().addPublicKeyHostKeyTypeExtractor(codec.getName(), codec);
                }
            } catch (Throwable ex) {
                Loggers.getLogger(PublicKeyCodecRegistry.class).warn(ex.getMessage(), ex);
            }
        }
    }

    public static PublicKeyCodecRegistry getInstance() {
        return INSTANCE;
    }
}
