package com.jn.agileway.ssh.client.impl.ganymedssh2;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.ganymedssh2.transport.hostkey.verifier.FromSsh2HostKeyVerifierAdapter;
import com.jn.agileway.ssh.client.impl.ganymedssh2.transport.hostkey.verifier.KnownHostsVerifier;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.OnClasses;

import java.io.File;
import java.util.List;

@OnClasses({"ch.ethz.ssh2.Connection"})
public class Ssh2ConnectionFactory extends AbstractSshConnectionFactory<Ssh2ConnectionConfig> {
    public Ssh2ConnectionFactory(){
        setName("ganymedssh2");
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return Ssh2Connection.class;
    }

    protected void setKnownHosts0(SshConnection connection, Ssh2ConnectionConfig sshConfig) {
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath());
        if (!paths.isEmpty()) {
            KnownHostsVerifier verifier = new KnownHostsVerifier(paths);
            verifier.setStrictHostKeyChecking(sshConfig.getStrictHostKeyChecking());
            connection.addHostKeyVerifier(new FromSsh2HostKeyVerifierAdapter(verifier));
        }
    }

    @Override
    public Ssh2ConnectionConfig newConfig() {
        return new Ssh2ConnectionConfig();
    }
}
