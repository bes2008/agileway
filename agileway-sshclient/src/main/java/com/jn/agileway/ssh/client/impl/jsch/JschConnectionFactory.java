package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.PromiscuousHostKeyVerifier;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.function.Predicate;

import java.io.File;
import java.util.List;
import java.util.Map;

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
            /*
            jsch.setConfigRepository(new ConfigRepository() {
                @Override
                public Config getConfig(String host) {
                    if (host.equals(sshConfig.getHost())) {
                        return sshConfig;
                    }
                    return null;
                }
            });

             */
            JSch.setLogger(new JschLoggerToSlf4jLogger());
        }
        conn.setJsch(jsch);
        setHostKeyVerifier(conn, sshConfig);
        /*
        if (!sshConfig.hasProperty("ConnectTimeout")) {
            sshConfig.getProps().put("ConnectTimeout", "5000"); // 5s
        }

         */
        Map<String, Object> props =sshConfig.getProps();
        Collects.forEach(props, new Consumer2<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                if(o instanceof String) {
                    JSch.setConfig(s, (String)o);
                }
            }
        });

    }

    protected void setKnownHosts(final SshConnection connection, final JschConnectionConfig sshConfig) {
        String filepath = sshConfig.getKnownHostsPath();
        List<File> paths = SshConfigs.getKnownHostsFiles(filepath);
        if (paths.isEmpty()) {
            HostKeyVerifier verifier = new PromiscuousHostKeyVerifier(sshConfig.getStrictHostKeyChecking() == StrictHostKeyChecking.NO);
            addHostKeyVerifier(connection, verifier);
            return;
        }
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
            sshConfig.setProperty("StrictHostKeyChecking", StrictHostKeyChecking.NO.getName());
        } else {
            sshConfig.setProperty("StrictHostKeyChecking", sshConfig.getStrictHostKeyChecking().getName());
        }
    }

    @Override
    public JschConnectionConfig newConfig() {
        return new JschConnectionConfig();
    }
}
