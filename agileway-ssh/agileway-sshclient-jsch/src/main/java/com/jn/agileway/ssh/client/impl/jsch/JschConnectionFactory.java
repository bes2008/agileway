package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.jsch.transport.hostkey.knownhosts.JschKnownHostsKeyRepository;
import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.OpenSSHKnownHosts;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.PromiscuousHostKeyVerifier;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.io.File;
import java.util.List;

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
                    return ConfigRepository.defaultConfig;
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
        String filepath = sshConfig.getKnownHostsPath();
        List<File> files = SshConfigs.getKnownHostsFiles(filepath);
        if (files.isEmpty()) {
            HostKeyVerifier verifier = new PromiscuousHostKeyVerifier(sshConfig.getStrictHostKeyChecking() == StrictHostKeyChecking.NO);
            addHostKeyVerifier(connection, verifier);
            return;
        }
        Collects.forEach(files, new Consumer<File>() {
            @Override
            public void accept(File file) {
                JschKnownHostsKeyRepository knownHostsKeyRepository = new JschKnownHostsKeyRepository(new OpenSSHKnownHosts(file));
                JSch jSch = ((JschConnection) connection).getJsch();
                jSch.setHostKeyRepository(knownHostsKeyRepository);
            }
        });
        sshConfig.setProperty("StrictHostKeyChecking", sshConfig.getStrictHostKeyChecking().getName());
    }

    @Override
    public JschConnectionConfig newConfig() {
        return new JschConnectionConfig();
    }
}
