package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;

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
        register(new SshDssPublicKeyCodec());
        register(new SshRsaPublicKeyCodec());
        Pipeline.of(ServiceLoader.load(PublicKeyCodec.class))
                .forEach(new Consumer<PublicKeyCodec>() {
                    @Override
                    public void accept(PublicKeyCodec publicKeyCodec) {
                        register(publicKeyCodec);
                    }
                });
    }

    public static PublicKeyCodecRegistry getInstance() {
        return INSTANCE;
    }
}
