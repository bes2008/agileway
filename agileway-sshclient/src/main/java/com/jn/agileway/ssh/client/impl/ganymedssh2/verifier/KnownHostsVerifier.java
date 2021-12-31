package com.jn.agileway.ssh.client.impl.ganymedssh2.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.KnownHostsFiles;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;

public class KnownHostsVerifier implements ServerHostKeyVerifier {
    private static final Logger logger = Loggers.getLogger(KnownHostsVerifier.class);
    private final KnownHosts knownHosts = new KnownHosts();
    private final List<File> knownHostsFiles = Collects.emptyArrayList();

    public KnownHostsVerifier(final List<File> files) {
        Collects.forEach(files, new Consumer<File>() {
            @Override
            public void accept(File file) {
                try {
                    knownHosts.addHostkeys(file);
                    knownHostsFiles.add(file);
                } catch (Throwable ex) {
                    // ignore it
                }
            }
        });
    }

    @Override
    public boolean verifyServerHostKey(final String hostname, int port, final String serverHostKeyAlgorithm, final byte[] serverHostKey) throws Exception {
        int result = knownHosts.verifyHostkey(hostname, serverHostKeyAlgorithm, serverHostKey);
        if (result == KnownHosts.HOSTKEY_IS_OK) {
            return true;
        }
        if (result == KnownHosts.HOSTKEY_IS_NEW) {
            Collects.forEach(this.knownHostsFiles, new Consumer<File>() {
                @Override
                public void accept(File file) {
                    try {
                        KnownHostsFiles.appendHostKeysToFile(file, new String[]{hostname}, serverHostKeyAlgorithm, serverHostKey);
                    } catch (Throwable ex) {
                        logger.error("write to known_hosts file {} fail, error: {}", file.getPath(), ex.getMessage(), ex);
                    }
                }
            });

            return true;
        }
        if (result == KnownHosts.HOSTKEY_HAS_CHANGED) {
            return false;
        }
        return false;
    }
}