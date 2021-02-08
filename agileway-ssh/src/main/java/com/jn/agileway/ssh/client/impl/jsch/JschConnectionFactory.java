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

    private static final List<String> strictHostKeyCheckingValues = Collects.newArrayList("yes", "ask", "no");

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

        // 如果设置了 StrictHostKeyChecking，怎根据值来判断
        // @see jsch Session

        // no: 关闭检查
        // yes: 开启检查，如果没有找到完全匹配的，则直接报错
        // ask: 开启检查，如果没有找到完全匹配的，则进行利用 UserInfo#promtYesNo 来进行询问，主要是问是否要删除原有的，添加新的

        String strictHostKeyChecking = null;

        if (sshConfig.hasProperty("StrictHostKeyChecking")) {
            strictHostKeyChecking = sshConfig.getProperty("StrictHostKeyChecking").toString();
            if (!strictHostKeyCheckingValues.contains(strictHostKeyChecking)) {
                strictHostKeyChecking = "ask";
            }
        } else {
            if (knownHostsPaths == null) {
                strictHostKeyChecking = "no";
            } else {
                strictHostKeyChecking = "ask";
            }
        }
        sshConfig.setProperty("StrictHostKeyChecking", strictHostKeyChecking);


        if ("ask".equals(strictHostKeyChecking) || "yes".equals(strictHostKeyChecking)) {
            if (!found) {
                if (Strings.isNotBlank(knownHostsPaths)) {
                    throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find any valid known_hosts file: {}", knownHostsPaths));
                } else {
                    throw new IllegalStateException(StringTemplates.formatWithPlaceholder("Can't find any valid known_hosts file"));
                }
            }
        }
    }
}
