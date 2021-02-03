package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractConnectionFactory;

public class SshjConnectionFactory extends AbstractConnectionFactory<SshjConfig> {
    @Override
    protected Class getDefaultConnectionClass() {
        return SshjConnection.class;
    }
}
