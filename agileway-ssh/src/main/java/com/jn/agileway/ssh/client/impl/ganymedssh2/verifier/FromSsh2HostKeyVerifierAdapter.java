package com.jn.agileway.ssh.client.impl.ganymedssh2.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;

import java.security.PublicKey;

public class FromSsh2HostKeyVerifierAdapter implements HostKeyVerifier {
    private ServerHostKeyVerifier delegate;

    public FromSsh2HostKeyVerifierAdapter(ServerHostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, int port, byte[] key) {
        try {
            return delegate.verifyServerHostKey(hostname, port, null, key);
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public boolean verify(String hostname, int port, PublicKey key) {
        try {
            return delegate.verifyServerHostKey(hostname, port, null, key.getEncoded());
        } catch (Throwable ex) {
            return false;
        }
    }
}
