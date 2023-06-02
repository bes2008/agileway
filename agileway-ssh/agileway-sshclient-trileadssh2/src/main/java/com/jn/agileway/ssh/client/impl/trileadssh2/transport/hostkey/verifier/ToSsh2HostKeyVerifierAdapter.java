package com.jn.agileway.ssh.client.impl.trileadssh2.transport.hostkey.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.trilead.ssh2.ServerHostKeyVerifier;

public class ToSsh2HostKeyVerifierAdapter implements ServerHostKeyVerifier {
    private HostKeyVerifier delegate;

    public ToSsh2HostKeyVerifierAdapter(HostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        return delegate.verify(hostname, port, serverHostKeyAlgorithm, serverHostKey);
    }
}
