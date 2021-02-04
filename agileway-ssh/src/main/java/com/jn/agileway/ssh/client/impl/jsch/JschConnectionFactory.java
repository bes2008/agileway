package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.annotation.Nullable;

public class JschConnectionFactory extends AbstractSshConnectionFactory<JschConnectionConfig> {
    @Nullable
    private JSch jsch;

    public void setJsch(JSch jsch) {
        this.jsch = jsch;
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return JschConnection.class;
    }

    @Override
    protected SshConnection createConnection(JschConnectionConfig connectionConfig) {
        JschConnection connection = new JschConnection();
        connection.setJsch(jsch);
        connection.setConfig(connectionConfig);
        return connection;
    }
}
