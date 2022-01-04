package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.AbstractNameable;

import java.security.PublicKey;

public abstract class AbstractPublicKeyCodec extends AbstractNameable implements PublicKeyCodec {
    @Override
    public final String get(PublicKey publicKey) {
        if (publicKey == null) {
            return null;
        }
        String algorithm =publicKey.getAlgorithm();
        if (isPublicKeyMatched(publicKey, algorithm)) {
            return getName();
        }

        return null;
    }

    protected abstract boolean isPublicKeyMatched(PublicKey publicKey, String algorithm);
}
