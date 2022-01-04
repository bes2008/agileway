package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.registry.GenericRegistry;

import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PublicKeyCodecRegistry extends GenericRegistry<PublicKeyCodec> {
    private static final PublicKeyCodecRegistry INSTANCE = new PublicKeyCodecRegistry();

    private PublicKeyCodecRegistry() {
        super(new ConcurrentHashMap<String, PublicKeyCodec>());
        register(new SshDssPublicKeyCodec());
        register(new SshRsaPublicKeyCodec());
    }


    public static PublicKeyCodecRegistry getInstance() {
        return INSTANCE;
    }
}
