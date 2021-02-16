package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnectionFactory;
import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.impl.sshj.verifier.FromSshHostKeyVerifierAdapter;
import com.jn.agileway.ssh.client.utils.SshConfigs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.io.file.Files;
import net.schmizz.sshj.common.KeyType;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;

import java.io.File;
import java.security.PublicKey;
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

    private void setKnownHosts(final SshConnection connection, final SshjConnectionConfig sshConfig) {
        List<File> paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath());
        if (paths.isEmpty()) {
            paths = SshConfigs.getKnownHostsFiles(sshConfig.getKnownHostsPath(), false);
        }
        if (!paths.isEmpty()) {
            Collects.forEach(paths, new Consumer<File>() {
                @Override
                public void accept(File file) {
                    try {
                        Files.makeFile(file);
                        HostKeyVerifier verifier = new OpenSSHKnownHosts(file) {
                            @Override
                            protected boolean hostKeyUnverifiableAction(String hostname, PublicKey key) {
                                try {
                                    this.write(new SimpleEntry(Marker.CA_CERT, hostname, KeyType.fromKey(key), key));
                                    return true;
                                } catch (Throwable ex) {
                                    logger.error(ex.getMessage(), ex);
                                    return false;
                                }
                            }
                        };
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
