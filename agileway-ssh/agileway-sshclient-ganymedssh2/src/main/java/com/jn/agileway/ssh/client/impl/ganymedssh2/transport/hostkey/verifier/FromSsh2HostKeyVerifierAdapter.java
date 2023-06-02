package com.jn.agileway.ssh.client.impl.ganymedssh2.transport.hostkey.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;

import java.util.List;

@Deprecated
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

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
        return null;
    }
}
