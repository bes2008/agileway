package com.jn.agileway.ssh.client.impl.synergy.verifier;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.sshtools.common.knownhosts.HostKeyVerification;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.components.SshPublicKey;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;

public class FromSynergyHostKeyVerificationAdapter implements HostKeyVerifier {
    private HostKeyVerification verification;

    public FromSynergyHostKeyVerificationAdapter(HostKeyVerification verification) {
        this.verification = verification;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, byte[] publicKey) {
        try {
            SshPublicKey pubKey = SshKeyUtils.getPublicKey(new ByteArrayInputStream(publicKey));
            return verification.verifyHost(hostname, pubKey);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, PublicKey publicKey) {
        return verify(hostname, port, serverHostKeyAlgorithm, publicKey.getEncoded());
    }
}
