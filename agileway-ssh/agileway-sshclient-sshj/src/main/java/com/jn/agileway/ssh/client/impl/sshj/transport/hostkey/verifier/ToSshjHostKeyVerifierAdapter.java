package com.jn.agileway.ssh.client.impl.sshj.transport.hostkey.verifier;

import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.security.PublicKey;
import java.util.List;

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
        return delegate.verify(hostname, port, null, key);
    }
    /**
     * It is necessary to connect with the type of algorithm that matches an existing know_host entry.
     * This will allow a match when we later verify with the negotiated key {@code HostKeyVerifier.verify}
     * @param hostname remote hostname
     * @param port     remote port
     * @return existing key types or empty list if no keys known for hostname
     */
    public List<String> findExistingAlgorithms(String hostname, int port){
        return delegate.findExistingAlgorithms(hostname, port);
    }
}
