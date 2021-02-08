package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.sshj.verifier.FromSshHostKeyVerifierAdapter;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;

import java.io.File;
import java.util.List;

public class SshjConnectionFactory extends AbstractSshConnectionFactory<SshjConnectionConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return SshjConnection.class;
    }

    @Override
    protected void postConstructConnection(final SshConnection connection, final SshjConnectionConfig sshConfig) {
        setKnownHosts(connection, sshConfig);
    }

    private void setKnownHosts(final SshConnection connection, final SshjConnectionConfig sshConfig){
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath());
        if (!paths.isEmpty()) {
            Collects.forEach(paths, new Consumer<File>() {
                @Override
                public void accept(File file) {
                    try {
                        HostKeyVerifier verifier = new OpenSSHKnownHosts(file);
                        FromSshHostKeyVerifierAdapter verifierAdapter = new FromSshHostKeyVerifierAdapter(verifier);
                        connection.addHostKeyVerifier(verifierAdapter);
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });

        }

    }
}
