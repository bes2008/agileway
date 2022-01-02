package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.OnClasses;

@OnClasses({"com.jcraft.jsch.JSch"})
public class JschConnectionFactory extends AbstractSshConnectionFactory<JschConnectionConfig> {
    @Nullable
    private JSch jsch;

    public JschConnectionFactory() {
        setName("jsch");
    }

    public void setJsch(JSch jsch) {
        this.jsch = jsch;
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return JschConnection.class;
    }

    @Override
    protected void postConstructConnection(SshConnection connection, final JschConnectionConfig sshConfig) {
        JschConnection conn = (JschConnection) connection;
        if (jsch == null) {
            jsch = new JSch();
            jsch.setConfigRepository(new ConfigRepository() {
                @Override
                public Config getConfig(String host) {
                    if (host.equals(sshConfig.getHost())) {
                        return sshConfig;
                    }
                    return null;
                }
            });
            JSch.setLogger(new JschLoggerToSlf4jLogger());
        }
        conn.setJsch(jsch);
        setHostKeyVerifier(conn, sshConfig);
        if (!sshConfig.hasProperty("ConnectTimeout")) {
            sshConfig.getProps().put("ConnectTimeout", "3000"); // 3s
        }
    }

    protected void setKnownHosts(final SshConnection connection, final JschConnectionConfig sshConfig) {
        super.setKnownHosts(connection, sshConfig);
        sshConfig.setProperty("StrictHostKeyChecking", sshConfig.getStrictHostKeyChecking().getName());
    }

    @Override
    public JschConnectionConfig newConfig() {
        return new JschConnectionConfig();
    }
}
