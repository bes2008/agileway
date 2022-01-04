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
import com.jn.langx.security.crypto.IllegalKeyException;
import com.jn.langx.security.crypto.digest.MessageDigests;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.security.PublicKey;

public class PublicKeyCodecs {

    public static final PublicKeyHostKeyTypeExtractor getPublicKeyHostKeyTypeExtractor() {
        return DefaultPublicKeyHostKeyTypeExtractor.getInstance();
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
        byte[] bytes = codec.encode(publicKey);
        if (bytes == null) {
            bytes = new byte[0];
        }
        return bytes;
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

    /**
     * Computes the fingerprint for a public key, in the standard SSH format, e.g. "4b:69:6c:72:6f:79:20:77:61:73:20:68:65:72:65:21"
     *
     * @param key the public key
     * @return the fingerprint
     * @see <a href="http://tools.ietf.org/html/draft-friedl-secsh-fingerprint-00">specification</a>
     */
    public static String getFingerprint(PublicKey key, String hashAlgorithm) {
        if (Strings.isBlank(hashAlgorithm)) {
            hashAlgorithm = "md5";
        }
        byte[] publicKey = encode(key);
        if (publicKey.length < 1) {
            throw new IllegalKeyException(key.getAlgorithm());
        }
        final String undelimited = MessageDigests.getDigestHexString(hashAlgorithm, publicKey);

        StringBuilder fp = new StringBuilder(undelimited.substring(0, 2));
        for (int i = 2; i <= undelimited.length() - 2; i += 2) {
            fp.append(":").append(undelimited.substring(i, i + 2));
        }
        return fp.toString();
    }

}
