package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.codec.PublicKeyCodecs;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.Charsets;

import java.security.PublicKey;

public class HostsKeyEntrys {
    public static byte[] getPublicKeyBytes(HostsKeyEntry entry) {
        Preconditions.checkNotNull(entry);
        Object publicKey = entry.getPublicKey();
        return toPublicKeyBytes(publicKey);
    }

    public static byte[] toPublicKeyBytes(Object publicKey) {
        if (publicKey == null) {
            return new byte[0];
        }
        if (publicKey instanceof PublicKey) {
            return PublicKeyCodecs.encode(null, (PublicKey) publicKey);
        }
        if (publicKey instanceof byte[]) {
            return (byte[]) publicKey;
        }
        if (publicKey instanceof String) {
            return Base64.decodeBase64((String) publicKey);
        }
        return publicKey.toString().getBytes(Charsets.UTF_8);
    }
}
