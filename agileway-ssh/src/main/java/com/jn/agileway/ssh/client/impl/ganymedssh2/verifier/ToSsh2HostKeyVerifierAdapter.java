package com.jn.agileway.ssh.client.impl.ganymedssh2.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
public class ToSsh2HostKeyVerifierAdapter implements ServerHostKeyVerifier {
    private HostKeyVerifier delegate;

    public ToSsh2HostKeyVerifierAdapter(HostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verifyServerHostKey(String hostname, int port, String serverHostKeyAlgorithm, byte[] serverHostKey) throws Exception {
        return delegate.verify(hostname, port, serverHostKey);
    }
}
