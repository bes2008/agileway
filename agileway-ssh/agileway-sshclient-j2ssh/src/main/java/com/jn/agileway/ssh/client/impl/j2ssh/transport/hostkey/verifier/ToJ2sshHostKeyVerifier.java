package com.jn.agileway.ssh.client.impl.j2ssh.transport.hostkey.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

public class ToJ2sshHostKeyVerifier implements HostKeyVerification {
    private HostKeyVerifier delegate;

    public ToJ2sshHostKeyVerifier(HostKeyVerifier delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verifyHost(String hostname, SshPublicKey sshPublicKey) throws TransportProtocolException {
        return delegate.verify(hostname, -1, sshPublicKey.getAlgorithmName(), sshPublicKey.getEncoded());
    }
}
