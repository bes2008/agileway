package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;

public class SshjConnectionFactory extends AbstractSshConnectionFactory<SshjConnectionConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return SshjConnection.class;
    }
}
