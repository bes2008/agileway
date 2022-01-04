package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.transport.hostkey.IllegalSshKeyException;
import com.jn.agileway.ssh.client.transport.hostkey.UnsupportedHostsKeyTypeException;
import com.jn.agileway.ssh.client.transport.hostkey.UnsupportedKeyException;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.DefaultPublicKeyHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.KeyBufferHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.KeyBytesHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.keytype.PublicKeyHostKeyTypeExtractor;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.io.Charsets;

import java.security.PublicKey;
import java.util.ServiceLoader;

public class PublicKeyCodecs {
    /**
     * 全局唯一
     */
    private static final PublicKeyHostKeyTypeExtractor publicKeyHostKeyTypeExtractor;

    static {
        PublicKeyHostKeyTypeExtractor extractor = null;
        extractor = Pipeline.of(ServiceLoader.<PublicKeyHostKeyTypeExtractor>load(PublicKeyHostKeyTypeExtractor.class)).findFirst();
        publicKeyHostKeyTypeExtractor = extractor == null ? new DefaultPublicKeyHostKeyTypeExtractor() : extractor;
    }

    public static final PublicKeyHostKeyTypeExtractor getPublicKeyHostKeyTypeExtractor() {
        return publicKeyHostKeyTypeExtractor;
    }

    public static PublicKey decode(byte[] key) {
        try {
            Buffer<?> buffer = new Buffer.PlainBuffer(key);
            final String keyType = extractKeyType(buffer);
            if (keyType == null) {
                throw new IllegalSshKeyException();
            }
            PublicKeyCodec codec = PublicKeyCodecRegistry.getInstance().get(keyType);
            if (codec == null) {
                throw new UnsupportedHostsKeyTypeException(keyType);
            }
            return codec.decode(buffer.remainingRawBytes());
        } catch (Buffer.BufferException e) {
            throw new IllegalSshKeyException(e);
        }
    }

    public static byte[] encode(PublicKey publicKey) {
        return encode(null, publicKey);
    }

    public static byte[] encode(String keyType, PublicKey publicKey) {
        if (keyType == null) {
            keyType = extractKeyType(publicKey);
        }
        if (keyType == null) {
            throw new UnsupportedKeyException(publicKey.getAlgorithm());
        }
        PublicKeyCodec codec = PublicKeyCodecRegistry.getInstance().get(keyType);
        if (codec == null) {
            throw new UnsupportedHostsKeyTypeException(keyType);
        }
        return codec.encode(publicKey);
    }

    public static String extractKeyType(PublicKey publicKey) {
        return getPublicKeyHostKeyTypeExtractor().get(publicKey);
    }

    public static String extractKeyType(byte[] publicKey) {
        return KeyBytesHostKeyTypeExtractor.INSTANCE.get(publicKey);
    }

    public static String extractKeyType(Buffer<?> publicKey) {
        return KeyBufferHostKeyTypeExtractor.INSTANCE.get(publicKey);
    }

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
