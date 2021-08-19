package com.jn.agileway.ssh.client.impl.ganymedssh2.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;

public class FromSsh2HostKeyVerifierAdapter implements HostKeyVerifier<byte[]> {
    private ServerHostKeyVerifier delegate;

    public FromSsh2HostKeyVerifierAdapter(ServerHostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, byte[] key) {
        try {
            return delegate.verifyServerHostKey(hostname, port, serverHostKeyAlgorithm, key);
        } catch (Throwable ex) {
            return false;
        }
    }

}
