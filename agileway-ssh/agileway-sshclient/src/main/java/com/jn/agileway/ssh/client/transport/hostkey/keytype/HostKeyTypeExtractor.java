package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import com.jn.langx.util.function.Supplier;

public interface HostKeyTypeExtractor<T> extends Supplier<T, String> {
    public static final String[] SUPPORTED_KEY_TYPES = {
            "ssh-dss",
            "ssh-rsa",
            "ecdsa-sha2-nistp256",
            "ecdsa-sha2-nistp384",
            "ecdsa-sha2-nistp521",
            "ssh-ed25519",
            "ssh-ed25519"
    };

    @Override
    String get(T publicKey);
}
