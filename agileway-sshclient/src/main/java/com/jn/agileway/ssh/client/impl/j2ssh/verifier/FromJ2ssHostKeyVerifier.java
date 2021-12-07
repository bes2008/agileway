package com.jn.agileway.ssh.client.impl.j2ssh.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.util.logging.Loggers;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;
import org.slf4j.Logger;


public class FromJ2ssHostKeyVerifier implements HostKeyVerifier<SshPublicKey> {
    private static final Logger logger = Loggers.getLogger(FromJ2ssHostKeyVerifier.class);
    private HostKeyVerification delegate;

    public FromJ2ssHostKeyVerifier(HostKeyVerification delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, SshPublicKey publicKey) {
        try {
            return delegate.verifyHost(hostname, publicKey);
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }
}
