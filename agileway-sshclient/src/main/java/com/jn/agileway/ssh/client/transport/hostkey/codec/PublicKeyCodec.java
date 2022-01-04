package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.Named;
import com.jn.langx.codec.CodecException;

import java.security.PublicKey;

public interface PublicKeyCodec extends Named {
    PublicKey decode(byte[] bytes) throws CodecException;

    byte[] encode(PublicKey publicKey) throws CodecException;
}
