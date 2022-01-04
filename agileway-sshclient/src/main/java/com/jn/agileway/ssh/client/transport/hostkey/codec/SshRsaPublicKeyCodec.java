package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.AbstractNameable;
import com.jn.langx.codec.CodecException;
import com.jn.langx.security.crypto.IllegalKeyException;
import com.jn.langx.security.crypto.key.PKIs;

import java.math.BigInteger;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

public class SshRsaPublicKeyCodec extends AbstractNameable implements PublicKeyCodec {
    public SshRsaPublicKeyCodec() {
        setName("ssh-rsa");
    }

    @Override
    public PublicKey decode(byte[] bytes) throws CodecException {
        Buffer<?> buf = new Buffer.PlainBuffer(bytes);
        final BigInteger e, n;
        try {
            e = buf.readMPInt();
            n = buf.readMPInt();

            return PKIs.getKeyFactory("RSA", null).generatePublic(new RSAPublicKeySpec(n, e));
        } catch (Buffer.BufferException be) {
            throw new IllegalKeyException(be);
        } catch (InvalidKeySpecException ex) {
            throw new IllegalKeyException(ex);
        }
    }

    @Override
    public byte[] encode(PublicKey publicKey) throws CodecException {
        Buffer<?> buf = new Buffer.PlainBuffer();
        final RSAPublicKey rsaKey = (RSAPublicKey) publicKey;
        buf.putString(getName())
                .putMPInt(rsaKey.getPublicExponent()) // e
                .putMPInt(rsaKey.getModulus()); // n
        return buf.array();
    }
}
