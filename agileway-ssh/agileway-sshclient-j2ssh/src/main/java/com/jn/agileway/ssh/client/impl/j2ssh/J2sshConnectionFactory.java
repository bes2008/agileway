package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.impl.j2ssh.transport.hostkey.verifier.FromJ2ssHostKeyVerifier;
import com.jn.agileway.ssh.client.impl.j2ssh.transport.hostkey.verifier.KnownHostsVerifier;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.OnClasses;
import com.sshtools.j2ssh.transport.HostKeyVerification;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;

import java.io.File;
import java.util.List;

@OnClasses({"com.sshtools.j2ssh.SshClient"})
public class J2sshConnectionFactory extends AbstractSshConnectionFactory<J2sshConnectionConfig> {

    public J2sshConnectionFactory(){
        setName("j2ssh");
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return J2sshConnection.class;
    }

    protected void setKnownHosts0(SshConnection connection, J2sshConnectionConfig sshConfig) {
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


    @Override
    public J2sshConnectionConfig newConfig() {
        return new J2sshConnectionConfig();
    }


}
