package com.jn.agileway.ssh.client.impl.j2ssh.transport.hostkey.verifier;

import com.sshtools.j2ssh.transport.AbstractKnownHostsKeyVerification;
import com.sshtools.j2ssh.transport.InvalidHostFileException;
import com.sshtools.j2ssh.transport.TransportProtocolException;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;

public class KnownHostsVerifier extends AbstractKnownHostsKeyVerification {

    public KnownHostsVerifier(String knownHostsFile) throws InvalidHostFileException {
        super(knownHostsFile);
    }

    @Override
    public void onHostKeyMismatch(String hostname, SshPublicKey sshPublicKey, SshPublicKey sshPublicKey1) throws TransportProtocolException {
        this.allowHost(hostname, sshPublicKey, false);
    }

    @Override
    public void onUnknownHost(String hostname, SshPublicKey sshPublicKey) throws TransportProtocolException {
        this.allowHost(hostname, sshPublicKey, true);
    }
}
