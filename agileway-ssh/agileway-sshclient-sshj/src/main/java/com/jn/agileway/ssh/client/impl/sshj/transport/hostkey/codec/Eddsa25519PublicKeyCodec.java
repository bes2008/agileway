package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.codec;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.transport.hostkey.codec.AbstractPublicKeyCodec;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.util.logging.Loggers;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveSpec;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.slf4j.Logger;

import java.security.PublicKey;
import java.util.Arrays;

public class Eddsa25519PublicKeyCodec extends AbstractPublicKeyCodec {
    private static final Logger logger = Loggers.getLogger(Eddsa25519PublicKeyCodec.class);

    public Eddsa25519PublicKeyCodec() {
        setName("ssh-ed25519");
    }

    @Override
    protected boolean isPublicKeyMatched(PublicKey publicKey, String algorithm) {
        return "EdDSA".equals(algorithm);
    }

    @Override
    public PublicKey decode(byte[] bytes) {
        try {
            Buffer buf = new Buffer.PlainBuffer(bytes);
            final int keyLen = buf.readUInt32AsInt();
            final byte[] p = new byte[keyLen];
            buf.readRawBytes(p);
            if (logger.isDebugEnabled()) {
                logger.debug("Key algo: {}, Key curve: 25519, Key Len: {}\np: {}",
                        getName(),
                        keyLen,
                        Arrays.toString(p)
                );
            }

            EdDSANamedCurveSpec ed25519 = EdDSANamedCurveTable.getByName("Ed25519");
            EdDSAPublicKeySpec publicSpec = new EdDSAPublicKeySpec(p, ed25519);
            return new Ed25519PublicKey(publicSpec);

        } catch (Buffer.BufferException be) {
            throw new SshException(be);
        }
    }

    @Override
    public byte[] encode(PublicKey publicKey) {
        Buffer buf = new Buffer.PlainBuffer();
        EdDSAPublicKey key = (EdDSAPublicKey) publicKey;
        buf.putBytes(key.getAbyte());
        return buf.array();
    }
}
