package com.jn.agileway.ssh.client.impl.j2ssh.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;
import com.sshtools.j2ssh.transport.publickey.SshPublicKeyFile;
import com.sshtools.j2ssh.transport.publickey.dsa.SshDssPublicKey;
import com.sshtools.j2ssh.transport.publickey.rsa.SshRsaPublicKey;

import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;

public class FromJ2ssHostKeyVerifier implements HostKeyVerifier {
    private HostKeyVerification delegate;

    public FromJ2ssHostKeyVerifier(HostKeyVerification delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, byte[] key) {
        try {
            SshPublicKeyFile keyFile = SshPublicKeyFile.parse(key);
            SshPublicKey publicKey = keyFile.toPublicKey();

            return delegate.verifyHost(hostname, publicKey);
        } catch (Throwable ex) {
            return false;
        }
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, PublicKey key) {
        try {
            SshPublicKey publicKey = null;
            if (key instanceof RSAPublicKey) {
                publicKey = new SshRsaPublicKey((RSAPublicKey) key);
            } else {
                publicKey = new SshDssPublicKey((DSAPublicKey) key);
            }
            return delegate.verifyHost(hostname, publicKey);
        } catch (Throwable ex) {
            return false;
        }
    }
}
