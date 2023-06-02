package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.codec;

public class Ecdsa512PublicKeyCodec  extends EcdsaXPublicKeyCodec {
    public Ecdsa512PublicKeyCodec() {
        setName("ecdsa-sha2-nistp512");
    }

    @Override
    protected int getFieldSize() {
        return 512;
    }
}

