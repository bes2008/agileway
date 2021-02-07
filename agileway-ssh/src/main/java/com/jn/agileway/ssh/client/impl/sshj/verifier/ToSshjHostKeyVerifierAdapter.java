package com.jn.agileway.ssh.client.impl.sshj.verifier;

import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.security.PublicKey;

public class ToSshjHostKeyVerifierAdapter implements HostKeyVerifier {

    private com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier delegate;

    public ToSshjHostKeyVerifierAdapter(com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        if (this.delegate == null) {
            return false;
        }
        return delegate.verify(hostname, port, key.getEncoded());
    }
}
