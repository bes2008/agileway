package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.codec.CodecException;
import com.jn.langx.security.crypto.IllegalKeyException;
import com.jn.langx.security.crypto.key.PKIs;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

public class SshDssPublicKeyCodec extends AbstractPublicKeyCodec {
    public SshDssPublicKeyCodec() {
        setName("ssh-dss");
    }

    @Override
    public PublicKey decode(byte[] bytes) throws CodecException {
        Buffer buf = new Buffer.PlainBuffer(bytes);
        BigInteger p, q, g, y;
        try {
            p = buf.readMPInt();
            q = buf.readMPInt();
            g = buf.readMPInt();
            y = buf.readMPInt();
        } catch (Buffer.BufferException be) {
            throw new IllegalKeyException(be);
        }
        try {
            return PKIs.getKeyFactory("DSA", null).generatePublic(new DSAPublicKeySpec(y, p, q, g));
        } catch (InvalidKeySpecException ex) {
            throw new IllegalKeyException();
        }
    }

    @Override
    public byte[] encode(PublicKey publicKey) throws CodecException {
        Buffer buf = new Buffer.PlainBuffer();
        final DSAPublicKey dsaKey = (DSAPublicKey) publicKey;
        buf.putString(getName())
                .putMPInt(dsaKey.getParams().getP()) // p
                .putMPInt(dsaKey.getParams().getQ()) // q
                .putMPInt(dsaKey.getParams().getG()) // g
                .putMPInt(dsaKey.getY()); // y

        return buf.array();
    }

    @Override
    protected boolean isPublicKeyMatched(PublicKey publicKey, String algorithm) {
        return "DSA".equals(algorithm);
    }
}
