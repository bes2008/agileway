package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.j2ssh.verifier.FromJ2ssHostKeyVerifier;
import com.jn.agileway.ssh.client.impl.j2ssh.verifier.KnownHostsVerifier;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import java.io.File;
import java.util.List;

public class J2sshConnectionFactory extends AbstractSshConnectionFactory<J2sshConnectionConfig> {

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return J2sshConnection.class;
    }

    @Override
    protected void postConstructConnection(SshConnection connection, J2sshConnectionConfig sshConfig) {
        setKnownHosts(connection, sshConfig);
    }

    private void setKnownHosts(SshConnection connection, J2sshConnectionConfig sshConfig) {
        String filepath = sshConfig.getKnownHostsPath();
        List<File> files = SshConfigs.getKnownHostsFiles(filepath);

        HostKeyVerification verifier = null;
        if (files.isEmpty()) {
            verifier = new IgnoreHostKeyVerification();
        } else {
            try {
                verifier = new KnownHostsVerifier(files.get(0).getAbsolutePath());
            } catch (Throwable ex) {
                throw new SshException(ex);
            }
        }
        connection.addHostKeyVerifier(new FromJ2ssHostKeyVerifier(verifier));
    }
}
