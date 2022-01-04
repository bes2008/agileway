package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.langx.Named;
import com.jn.langx.codec.ICodec;

import java.security.PublicKey;

public interface PublicKeyCodec extends ICodec<PublicKey>, Named {
    PublicKey decode(byte[] bytes);

    byte[] encode(PublicKey publicKey);
}
