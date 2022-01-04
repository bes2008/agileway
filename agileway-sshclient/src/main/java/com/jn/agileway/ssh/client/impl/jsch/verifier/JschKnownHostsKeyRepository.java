package com.jn.agileway.ssh.client.impl.jsch.verifier;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.HostKeyRepository;
import com.jcraft.jsch.UserInfo;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.*;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Functions;
import com.jn.langx.util.function.Predicate;

import java.util.List;

public class JschKnownHostsKeyRepository implements HostKeyRepository {
    private OpenSSHKnownHosts knownHosts;

    public JschKnownHostsKeyRepository(OpenSSHKnownHosts knownHosts) {
        this.knownHosts = knownHosts;
    }

    private static List<String> supportedKeyTypes = Collects.immutableArrayList(
            "ssh-dss",
            "ssh-rsa",
            "ecdsa-sha2-nistp256",
            "ecdsa-sha2-nistp384",
            "ecdsa-sha2-nistp521"
    );

    @Override
    public int check(String host, byte[] key) {
        if (host == null) {
            return NOT_INCLUDED;
        }

        String keyType = new Buffer.PlainBuffer(key).readString();
        if (!supportedKeyTypes.contains(keyType)) {
            return NOT_INCLUDED;
        }
        List<HostsKeyEntry> entries = knownHosts.find(host, keyType);
        if(Objs.isNotEmpty(entries)){
            HostsKeyEntry entry = entries.get(0);
            if(entry.verify(key)){
                return OK;
            }else{
                return CHANGED;
            }
        }

        return NOT_INCLUDED;
    }

    @Override
    public void add(HostKey hostkey, UserInfo ui) {
        String host = hostkey.getHost();
        HostsKeyEntry entry = null;
        if (Strings.startsWith(host, HashedHostsKeyEntry.HOSTS_FLAG)) {
            entry = new HashedHostsKeyEntry(host, hostkey.getType(), hostkey.getKey());
        } else {
            entry = new SimpleHostsKeyEntry(host, hostkey.getType(), hostkey.getKey());
        }
        if (entry.isValid()) {
            knownHosts.add(entry);
        }
    }

    @Override
    public void remove(String host, String type) {
        knownHosts.remove(host, type);
    }

    @Override
    public void remove(final String host, final String type, final byte[] key) {
        final List<HostsKeyEntry> entries = knownHosts.find(host, type);
        List<HostsKeyEntry> found = Pipeline.of(entries)
                .filter(new Predicate<HostsKeyEntry>() {
                    @Override
                    public boolean test(HostsKeyEntry entry) {
                        return Pipeline.<HostsKeyEntry>of(new SimpleHostsKeyEntry(host, type, key), new HashedHostsKeyEntry(host, type, key))
                                .anyMatch(Functions.equalsPredicate(entry));
                    }
                }).asList();
        if (Objs.isNotEmpty(found)) {
            knownHosts.remove(host, type);
        }
    }

    @Override
    public String getKnownHostsRepositoryID() {
        return knownHosts.getId();
    }

    @Override
    public HostKey[] getHostKey() {
        return toJschHostKeys(knownHosts.getAll());
    }

    @Override
    public HostKey[] getHostKey(String host, String type) {
        return toJschHostKeys(knownHosts.find(host, type));
    }

    private static HostKey[] toJschHostKeys(List<HostsKeyEntry> entries) {
        return Pipeline.of(entries)
                .map(new Function<HostsKeyEntry, HostKey>() {
                    @Override
                    public HostKey apply(HostsKeyEntry entry) {
                        return toJschHostKey(entry);
                    }
                })
                .clearNulls()
                .toArray(HostKey[].class);
    }

    private static HostKey toJschHostKey(HostsKeyEntry entry) {
        if (entry != null && entry.isValid()) {
            try {
                return new HostKey(entry.getMarker().getName(), entry.getHosts(), 0, HostsKeyEntrys.toPublicKeyBytes(entry.getPublicKey()), null);
            } catch (Throwable ex) {
                return null;
            }
        }
        return null;
    }

}
