package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;

public class SynergyConnectionFactory extends AbstractSshConnectionFactory<SynergyConnectionConfig> {
    @Override
    protected void postConstructConnection(SshConnection connection, SynergyConnectionConfig sshConfig) {

    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return SynergyConnection.class;
    }

    @Override
    public SynergyConnectionConfig newConfig() {
        return new SynergyConnectionConfig();
    }
}
