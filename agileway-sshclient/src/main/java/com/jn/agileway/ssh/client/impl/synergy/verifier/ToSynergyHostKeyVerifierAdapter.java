package com.jn.agileway.ssh.client.impl.synergy.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.sshtools.common.knownhosts.HostKeyVerification;
import com.sshtools.common.ssh.SshException;
import com.sshtools.common.ssh.components.SshPublicKey;


public class ToSynergyHostKeyVerifierAdapter implements HostKeyVerification {
    private HostKeyVerifier<byte[]> verifier;

    public ToSynergyHostKeyVerifierAdapter(HostKeyVerifier verifier) {
        this.verifier = verifier;
    }

    @Override
    public boolean verifyHost(String host, SshPublicKey sshPublicKey) throws SshException {
        boolean ret = this.verifier.verify(host, -1, sshPublicKey.getAlgorithm(), sshPublicKey.getEncoded());
        return ret;
    }
}
