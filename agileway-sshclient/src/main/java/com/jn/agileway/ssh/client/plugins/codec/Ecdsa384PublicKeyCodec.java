package com.jn.agileway.ssh.client.plugins.codec;


public class Ecdsa384PublicKeyCodec extends EcdsaXPublicKeyCodec {
    public Ecdsa384PublicKeyCodec() {
        setName("ecdsa-sha2-nistp384");
    }

    @Override
    protected int getFieldSize() {
        return 384;
    }
}
