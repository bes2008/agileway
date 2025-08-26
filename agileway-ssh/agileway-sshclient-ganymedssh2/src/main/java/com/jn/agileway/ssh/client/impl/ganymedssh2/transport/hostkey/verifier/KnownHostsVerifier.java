package com.jn.agileway.ssh.client.impl.ganymedssh2.transport.hostkey.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
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
    private StrictHostKeyChecking strictHostKeyChecking;

    private static final List<String> supportedAlgorithms = Collects.immutableArrayList("ssh-dss", "ssh-rsa");

    public StrictHostKeyChecking getStrictHostKeyChecking() {
        return strictHostKeyChecking;
    }

    public void setStrictHostKeyChecking(StrictHostKeyChecking strictHostKeyChecking) {
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

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
        if (!supportedAlgorithms.contains(serverHostKeyAlgorithm)) {
            return false;
        }
        int result = knownHosts.verifyHostkey(hostname, serverHostKeyAlgorithm, serverHostKey);
        if (result == KnownHosts.HOSTKEY_IS_OK) {
            return true;
        }
        if (strictHostKeyChecking == StrictHostKeyChecking.NO) {
            if (result == KnownHosts.HOSTKEY_IS_NEW) {
                Collects.forEach(this.knownHostsFiles, new Consumer<File>() {
                    @Override
                    public void accept(File file) {
                        synchronized (knownHosts) {
                            try {
                                // 放到cache
                                knownHosts.addHostkey(new String[]{hostname}, serverHostKeyAlgorithm, serverHostKey);
                                // 写入文件
                                KnownHostsFiles.appendHostKeysToFile(file, new String[]{hostname}, serverHostKeyAlgorithm, serverHostKey);
                            } catch (Throwable ex) {
                                logger.error("write to known_hosts file {} fail, error: {}", file.getPath(), ex.getMessage(), ex);
                            }
                        }
                    }
                });

                return true;
            }
            if (result == KnownHosts.HOSTKEY_HAS_CHANGED) {

                return true;
            }
            return true;
        } else {
            // 进入这个逻辑，已经说明了验证失败了。
            // result == KnownHosts.HOSTKEY_IS_NEW 或者 result == KnownHosts.HOSTKEY_HAS_CHANGED 只是在 StrictHostKeyChecking.NO时才有用的
            return false;
        }
    }
}