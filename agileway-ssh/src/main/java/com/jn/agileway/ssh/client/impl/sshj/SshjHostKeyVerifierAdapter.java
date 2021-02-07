package com.jn.agileway.ssh.client.impl.sshj;

import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.security.PublicKey;

public class SshjHostKeyVerifierAdapter implements HostKeyVerifier {

    private com.jn.agileway.ssh.client.transport.verifier.HostKeyVerifier delegate;

    SshjHostKeyVerifierAdapter(com.jn.agileway.ssh.client.transport.verifier.HostKeyVerifier delegate) {
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
