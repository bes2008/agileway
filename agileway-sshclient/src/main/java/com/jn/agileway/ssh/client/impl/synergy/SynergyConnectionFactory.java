package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.synergy.verifier.FromSynergyHostKeyVerificationAdapter;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.annotation.OnClasses;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.logging.Loggers;
import com.sshtools.common.knownhosts.KnownHostsFile;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

@OnClasses({"com.sshtools.client.SshClient"})
public class SynergyConnectionFactory extends AbstractSshConnectionFactory<SynergyConnectionConfig> {
    private static final Logger logger = Loggers.getLogger(SynergyConnectionFactory.class);

    public SynergyConnectionFactory(){
        setName("synergy");
    }

    @Override
    protected void postConstructConnection(SshConnection connection, SynergyConnectionConfig sshConfig) {
        configKnownHosts(connection, sshConfig);
    }

    @Override
    protected Class<?> getDefaultConnectionClass() {
        return SynergyConnection.class;
    }

    @Override
    public SynergyConnectionConfig newConfig() {
        return new SynergyConnectionConfig();
    }


    private void configKnownHosts(final SshConnection connection, SynergyConnectionConfig sshConfig) {
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath());
        if (paths.isEmpty()) {
            paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath(), false);
        }
        Collects.forEach(paths, new Consumer<File>() {
            @Override
            public void accept(File file) {
                try {
                    KnownHostsFile f = new KnownHostsFile(file);
                    connection.addHostKeyVerifier(new FromSynergyHostKeyVerificationAdapter(f));
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
    }
}
