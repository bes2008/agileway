package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

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
            return ((PublicKey) publicKey).getEncoded();// 目前的写法是原始的jca key，并不是ssh 格式的key
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
