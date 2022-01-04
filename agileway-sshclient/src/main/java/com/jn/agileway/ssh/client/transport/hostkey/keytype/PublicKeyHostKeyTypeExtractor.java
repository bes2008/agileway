package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import java.security.PublicKey;

public interface PublicKeyHostKeyTypeExtractor extends HostKeyTypeExtractor<PublicKey> {
    @Override
    String get(PublicKey publicKey);
}
