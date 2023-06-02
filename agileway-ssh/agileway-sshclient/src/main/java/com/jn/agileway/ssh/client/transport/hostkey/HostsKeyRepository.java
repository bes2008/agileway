package com.jn.agileway.ssh.client.transport.hostkey;

import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;

import java.util.List;

public interface HostsKeyRepository {
    String getId();
    /**
     * Removes a host key if there exists matched key with
     * <code>host</code>, <code>type</code>.
     */
    void remove(String host, String type);
    void remove(HostsKeyEntry entry);
    void add(HostsKeyEntry entry);

    /**
     * Returns a list for host keys managed in this repository.
     *
     * @see #find (String host, String type)
     */
    List<HostsKeyEntry> getAll();

    List<HostsKeyEntry> find(@NonNull String host, @Nullable String keyType);
}
