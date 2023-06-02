package com.jn.agileway.ssh.client.impl.synergy.transport.hostkey.verifier;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.sshtools.common.knownhosts.HostKeyVerification;
import com.sshtools.common.ssh.components.SshPublicKey;

import java.util.List;

@Deprecated
public class FromSynergyHostKeyVerificationAdapter implements HostKeyVerifier<SshPublicKey> {
    private HostKeyVerification verification;

    public FromSynergyHostKeyVerificationAdapter(HostKeyVerification verification) {
        this.verification = verification;
    }


    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, SshPublicKey publicKey) {
        try {
            return verification.verifyHost(hostname, publicKey);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public List<String> findExistingAlgorithms(String hostname, int port) {
        return null;
    }
}
