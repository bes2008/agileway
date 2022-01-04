package com.jn.agileway.ssh.client.transport.hostkey.codec;

import com.jn.agileway.ssh.client.transport.hostkey.keytype.PublicKeyHostKeyTypeExtractor;
import com.jn.langx.Named;
import com.jn.langx.codec.ICodec;

import java.security.PublicKey;

public interface PublicKeyCodec extends ICodec<PublicKey>, Named, PublicKeyHostKeyTypeExtractor {
    PublicKey decode(byte[] bytes);

    byte[] encode(PublicKey publicKey);

    /**
     *
     * @return ssh key type
     *
     * 可选值：
     *  <pre>
          ssh-dss,
     *    ssh-rsa,
     *    ecdsa-sha2-nistp256,
     *    ecdsa-sha2-nistp384,
     *    ecdsa-sha2-nistp521
     *  </pre>
     */
    @Override
    String getName();


    @Override
    String get(PublicKey publicKey);
}
