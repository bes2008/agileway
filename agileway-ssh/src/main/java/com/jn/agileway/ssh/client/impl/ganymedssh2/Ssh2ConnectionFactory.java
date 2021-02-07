package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.KnownHosts;
import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;

import java.io.File;
import java.util.List;

public class Ssh2ConnectionFactory extends AbstractSshConnectionFactory<Ssh2ConnectionConfig> {
    @Override
    protected Class<?> getDefaultConnectionClass() {
        return Ssh2Connection.class;
    }

    @Override
    protected void postConstructConnection(SshConnection connection, Ssh2ConnectionConfig sshConfig) {

    }

    private void addHostKeyVerifiers(SshConnection connection, Ssh2ConnectionConfig sshConfig) {
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPaths());

        if (!paths.isEmpty()) {
            final KnownHosts knownHosts = new KnownHosts();
            Collects.forEach(paths, new Consumer<File>() {
                @Override
                public void accept(File file) {
                    try {
                        knownHosts.addHostkeys(file);
                    } catch (Throwable ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            });


        }
    }
}
