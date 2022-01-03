package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.codec.CodecException;

import java.security.PublicKey;

public interface JcaPublicKeyCodec extends PublicKeyCodec<PublicKey> {
    @Override
    PublicKey decode(byte[] bytes) throws CodecException;

    @Override
    byte[] encode(PublicKey publicKey) throws CodecException;
}
