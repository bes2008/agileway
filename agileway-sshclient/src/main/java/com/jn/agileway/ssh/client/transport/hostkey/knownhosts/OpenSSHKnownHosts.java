package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostsKeyRepository;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OpenSSHKnownHosts extends AbstractInitializable implements HostsKeyRepository {
    private static final Logger logger = Loggers.getLogger(OpenSSHKnownHosts.class);
    protected final File khFile;
    protected final Set<HostsKeyEntry> entries = new LinkedHashSet<HostsKeyEntry>();
    private String id;
    public OpenSSHKnownHosts(File khFile) {
        this.khFile = khFile;
        init();
        this.id = khFile.getAbsolutePath();
    }

    @Override
    public String getId() {
        return id;
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


    public void rewrite() {
        try {
            KnownHostsFiles.rewrite(this.khFile, this.entries);
        } catch (IOException ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }

    public void add(HostsKeyEntry entry) {
        if (entry != null) {
            synchronized (this) {
                if (this.entries.add(entry)) {
                    rewrite();
                }
            }
        }
    }

    @Override
    public void remove(final String host, String type) {
        List<HostsKeyEntry> found = find(host, type);
        if (Objs.isNotEmpty(found)) {
            synchronized (this) {
                Pipeline.of(found).forEach(new Consumer<HostsKeyEntry>() {
                    @Override
                    public void accept(HostsKeyEntry entry) {
                        if (entry instanceof SimpleHostsKeyEntry) {
                            SimpleHostsKeyEntry hostsKeyEntry = (SimpleHostsKeyEntry) entry;
                            String hostsString = hostsKeyEntry.getHosts();
                            List<String> hosts = Collects.asList(Strings.split(hostsString, ","));
                            hosts.remove(host);
                            if (hosts.isEmpty()) {
                                OpenSSHKnownHosts.this.entries.remove(entry);
                            }
                        } else if (entry instanceof HashedHostsKeyEntry) {
                            OpenSSHKnownHosts.this.entries.remove(entry);
                        }
                    }
                });
                rewrite();
            }
        }
    }

    public void remove(HostsKeyEntry entry) {
        synchronized (this) {
            if (this.entries.remove(entry)) {
                rewrite();
            }
        }
    }

    @Override
    public List<HostsKeyEntry> getAll() {
        return Pipeline.of(this.entries).asList();
    }

    @Override
    public List<HostsKeyEntry> find(final String host, final String keyType) {
        Preconditions.checkNotNull(host);
        return Pipeline.of(this.entries)
                .filter(new Predicate<HostsKeyEntry>() {
                    @Override
                    public boolean test(HostsKeyEntry entry) {
                        return entry.applicableTo(host, keyType);
                    }
                }).asList();
    }
}