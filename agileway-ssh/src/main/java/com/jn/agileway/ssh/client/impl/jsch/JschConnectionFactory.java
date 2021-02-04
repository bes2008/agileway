package com.jn.agileway.ssh.client.impl.jsch;

import com.jn.agileway.ssh.client.AbstractConnectionFactory;

public class JschConnectionFactory extends AbstractConnectionFactory<JschConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return JschConnection.class;
    }
}
