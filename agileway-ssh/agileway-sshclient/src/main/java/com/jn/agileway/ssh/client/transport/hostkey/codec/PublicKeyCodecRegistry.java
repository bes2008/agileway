package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.transport.hostkey.keytype.DefaultPublicKeyHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.PublicKeyHostKeyTypeExtractor;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.spi.CommonServiceProvider;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PublicKeyCodecRegistry extends GenericRegistry<PublicKeyCodec> {
    private static PublicKeyCodecRegistry INSTANCE;

    private PublicKeyCodecRegistry() {
        super(new ConcurrentHashMap<String, PublicKeyCodec>());
    }

    @Override
    protected void doInit() throws InitializationException {
        Iterator<PublicKeyCodec> loader = CommonServiceProvider.loadService(PublicKeyCodec.class).iterator();
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
        if (INSTANCE == null) {
            synchronized (PublicKeyCodecRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PublicKeyCodecRegistry();
                    INSTANCE.init();
                }
            }
        }
        return INSTANCE;
    }
}
