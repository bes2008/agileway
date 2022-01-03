package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public abstract class OpenSSHKnownHosts extends AbstractInitializable implements HostKeyVerifier {
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
                entries = load();
                this.entries.addAll(entries);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    protected List<HostsKeyEntry> load() throws IOException {
        List<HostsKeyEntry> entries = KnownHostsFiles.read(khFile);
        return entries;
    }

    protected String getKeyType(PublicKey publicKey) {
        if (publicKey instanceof RSAPublicKey) {
            return HostKeyType.SSH_RSA.getName();
        }
        if (publicKey instanceof DSAPublicKey) {
            return HostKeyType.SSH_DSS.getName();
        }
        if ("ECDSA".equals(publicKey.getAlgorithm())) {
            // 此时可能有多种，按 nistp256 曲线来
            return "ecdsa-sha2-nistp256";
        }
        return null;
    }

    @Override
    public boolean verify(@NonNull String hostname, int port, @Nullable String serverHostKeyAlgorithm, @NonNull Object publicKey) {
        Preconditions.checkNotNull(hostname);
        Preconditions.checkNotNull(publicKey);

        if (serverHostKeyAlgorithm == null && publicKey instanceof PublicKey) {
            serverHostKeyAlgorithm = getKeyType((PublicKey) publicKey);
        }
        if (Strings.isEmpty(serverHostKeyAlgorithm)) {
            return false;
        }

        final String adjustedHostname = (port != 22 && port > 0) ? ("[" + hostname + "]:" + port) : hostname;

        for (HostsKeyEntry e : entries) {
            try {
                if (e.applicableTo(adjustedHostname, serverHostKeyAlgorithm)) {
                    if (!e.verify(publicKey)) {
                        return hostKeyChanged(e, adjustedHostname, publicKey);
                    }
                    return true;
                }
            } catch (IOException ioe) {
                logger.error("Error with {}: {}", e, ioe);
                return false;
            }
        }

        return unknownHostKey(adjustedHostname, serverHostKeyAlgorithm, publicKey);
    }

    protected abstract boolean hostKeyChanged(HostsKeyEntry entry, String hostname, Object publicKey) throws IOException;

    protected abstract boolean unknownHostKey(String hostname, String keyType, Object publicKey);

    public void rewrite() throws IOException {
        KnownHostsFiles.rewrite(this.khFile, this.entries);
    }

    public void add(HostsKeyEntry entry) throws IOException {
        if (entry != null) {
            synchronized (this) {
                if (this.entries.add(entry)) {
                    KnownHostsFiles.appendHostKeysToFile(this.khFile, entry);
                }
            }
        }
    }

}