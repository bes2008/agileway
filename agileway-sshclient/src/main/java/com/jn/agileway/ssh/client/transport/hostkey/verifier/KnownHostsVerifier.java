package com.jn.agileway.ssh.client.transport.hostkey.verifier;

import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HashedHostsKeyEntry;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.OpenSSHKnownHosts;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.SimpleHostsKeyEntry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class KnownHostsVerifier extends OpenSSHKnownHosts {
    private static final Logger logger = Loggers.getLogger(KnownHostsVerifier.class);
    private StrictHostKeyChecking strictHostKeyChecking;

    public KnownHostsVerifier(File file, StrictHostKeyChecking strictHostKeyChecking) {
        super(file);
        this.strictHostKeyChecking = strictHostKeyChecking;
    }

    @Override
    protected boolean hostKeyChanged(HostsKeyEntry entry, String hostname, Object publicKey) throws IOException {
        Preconditions.checkNotNull(strictHostKeyChecking);
        logger.warn("host key changed: hostname: {}, keyType:{}", hostname, entry.getKeyType());
        if (this.strictHostKeyChecking == StrictHostKeyChecking.NO) {
            if (entry instanceof SimpleHostsKeyEntry) {
                synchronized (this) {
                    SimpleHostsKeyEntry hostsKeyEntry = (SimpleHostsKeyEntry) entry;
                    String hostsString = hostsKeyEntry.getHosts();
                    List<String> hosts = Collects.asList(Strings.split(hostsString, ","));
                    hosts.remove(hostname);
                    if (hosts.isEmpty()) {
                        this.entries.remove(entry);
                    }
                    this.entries.add(new SimpleHostsKeyEntry(hostname, entry.getKeyType(), publicKey));
                    this.rewrite();
                }
                return true;
            } else if (entry instanceof HashedHostsKeyEntry) {
                synchronized (this) {
                    this.entries.remove(entry);
                    this.entries.add(new SimpleHostsKeyEntry(hostname, entry.getKeyType(), publicKey));
                    this.rewrite();
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    @Override
    protected boolean unknownHostKey(String hostname, String keyType, Object publicKey) {
        Preconditions.checkNotNull(strictHostKeyChecking);
        logger.info("unknown host key: hostname: {}, keyType:{}", hostname, keyType);
        if (this.strictHostKeyChecking == StrictHostKeyChecking.NO) {
            SimpleHostsKeyEntry entry = new SimpleHostsKeyEntry(hostname, keyType, publicKey);
            if (entry.isValid()) {
                synchronized (this) {
                    this.entries.add(entry);
                    try {
                        this.rewrite();
                    } catch (IOException ex) {
                        this.entries.remove(entry);
                        logger.warn(ex.getMessage(), ex);
                        return false;
                    }
                }
                return true;
            }
            return false;
        } else {
            return false;
        }
    }
}
