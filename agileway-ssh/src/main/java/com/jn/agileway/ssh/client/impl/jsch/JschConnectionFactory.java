package com.jn.agileway.ssh.client.impl.jsch;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;

public class JschConnectionFactory extends AbstractSshConnectionFactory<JschConnectionConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return JschConnection.class;
    }
}
