package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.function.Predicate;

import java.io.File;
import java.util.List;

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
        }
        conn.setJsch(jsch);
        setKnownHosts(conn, sshConfig);
        if (!sshConfig.hasProperty("ConnectTimeout")) {
            sshConfig.getProps().put("ConnectTimeout", "3000"); // 3s
        }
    }

    private void setKnownHosts(final SshConnection connection, final JschConnectionConfig sshConfig) {
        String knownHostsPaths = sshConfig.getKnownHostsPaths();
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPaths());
        boolean found = false;
        if (!paths.isEmpty()) {
            found = Collects.anyMatch(paths, new Predicate<File>() {
                @Override
                public boolean test(File file) {
                    try {
                        jsch.setKnownHosts(file.getPath());
                        return true;
                    } catch (Throwable ex) {
                        return false;
                    }
                }
            });

        }
        if (!found) {
            MapAccessor mapAccessor = new MapAccessor(sshConfig.getProps());
            // @see jsch Session
            boolean strictHostKeyChecking = mapAccessor.getBoolean("StrictHostKeyChecking", true);
            if (strictHostKeyChecking) {
                if (Strings.isNotBlank(knownHostsPaths)) {
                    throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find any valid known_hosts file: {}", knownHostsPaths));
                } else {
                    throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find any valid known_hosts file"));
                }
            }
        }
    }
}
