package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class OpenSSHKnownHosts extends AbstractInitializable implements HostKeyVerifier<PublicKey> {
    private static final Logger logger = Loggers.getLogger(OpenSSHKnownHosts.class);
    protected final File khFile;
    protected final Set<HostsKeyEntry> entries = new LinkedHashSet<HostsKeyEntry>();

    public OpenSSHKnownHosts(File khFile) {
        this.khFile = khFile;
        init();
    }

    @Override
    protected void doInit() throws InitializationException {
        if (khFile.exists()) {
            List<HostsKeyEntry> entries = null;
            try {
                entries = KnownHostsFiles.read(khFile);
                if (entries != null) {
                    this.entries.addAll(entries);
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    protected List<HostsKeyEntry> load() throws IOException {
        List<HostsKeyEntry> entries = KnownHostsFiles.read(khFile);
        return entries;
    }

    @Override
    public boolean verify(String hostname, int port, String serverHostKeyAlgorithm, PublicKey publicKey) {
        final HostKeyType type = HostKeyType.guessKeyType(publicKey);

        if (type == null) {
            return false;
        }

        final String adjustedHostname = (port != 22) ? "[" + hostname + "]:" + port : hostname;

        for (HostsKeyEntry e : entries) {
            try {
                if (e.applicableTo(adjustedHostname, type.getName())) {
                    return e.verify(publicKey) || hostKeyChanged(e, adjustedHostname, publicKey);
                }
            } catch (IOException ioe) {
                logger.error("Error with {}: {}", e, ioe);
                return false;
            }
        }

        return unknownHostKey(adjustedHostname, publicKey);
    }

    protected abstract boolean hostKeyChanged(HostsKeyEntry entry, String hostname, PublicKey publicKey) throws IOException;

    protected abstract boolean unknownHostKey(String hostname, PublicKey publicKey);

    public void rewrite() throws IOException {
        KnownHostsFiles.rewrite(this.khFile, this.entries);
    }

    public void add(HostsKeyEntry entry) throws IOException {
        if (entry != null) {
            if (this.entries.add(entry)) {
                KnownHostsFiles.appendHostKeysToFile(this.khFile, entry);
            }
        }
    }

}