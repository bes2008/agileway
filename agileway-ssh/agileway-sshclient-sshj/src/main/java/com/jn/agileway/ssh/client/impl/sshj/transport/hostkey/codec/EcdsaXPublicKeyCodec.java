package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.codec;

import com.jn.agileway.ssh.client.transport.hostkey.codec.AbstractPublicKeyCodec;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.security.crypto.IllegalKeyException;

import java.security.PublicKey;

public abstract class EcdsaXPublicKeyCodec extends AbstractPublicKeyCodec {

    @Override
    public PublicKey decode(byte[] bytes) {
        Buffer buf = new Buffer.PlainBuffer(bytes);
        try {
            return EcdsaXs.readPubKeyFromBuffer(buf, "" + getFieldSize());
        } catch (Throwable ex) {
            throw new IllegalKeyException();
        }
    }

    @Override
    public byte[] encode(PublicKey publicKey) {
        Buffer buf = new Buffer.PlainBuffer();
        EcdsaXs.writePubKeyContentsIntoBuffer(publicKey, buf);
        return buf.array();
    }

    @Override
    protected boolean isPublicKeyMatched(PublicKey publicKey, String algorithm) {
        return EcdsaXs.isECKeyWithFieldSize(publicKey, getFieldSize());
    }

    protected abstract int getFieldSize();
}