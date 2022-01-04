package com.jn.agileway.ssh.client.plugins.codec;


public class Ecdsa256PublicKeyCodec extends EcdsaXPublicKeyCodec {
    public Ecdsa256PublicKeyCodec() {
        setName("ecdsa-sha2-nistp256");
    }

    @Override
    protected int getFieldSize() {
        return 256;
    }
}
