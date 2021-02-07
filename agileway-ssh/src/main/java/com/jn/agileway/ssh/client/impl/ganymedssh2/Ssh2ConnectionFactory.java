package com.jn.agileway.ssh.client.impl.ganymedssh2;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;

public class Ssh2ConnectionFactory extends AbstractSshConnectionFactory<Ssh2ConnectionConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return Ssh2Connection.class;
    }

    @Override
    protected void postConstructConnection(SshConnection connection, Ssh2ConnectionConfig sshConfig) {

    }
}
