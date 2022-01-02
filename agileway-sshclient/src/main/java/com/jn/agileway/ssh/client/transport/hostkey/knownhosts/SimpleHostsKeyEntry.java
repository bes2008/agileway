package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.security.PublicKey;
import java.util.Set;

public class SimpleHostsKeyEntry extends AbstractHostsKeyEntry {
    private transient final Set<String> hostList = Collects.emptyHashSet();

    public SimpleHostsKeyEntry() {
    }

    public SimpleHostsKeyEntry(String hosts, HostKeyType keyType, PublicKey publicKey) {
        this(null, hosts, keyType, publicKey);
    }

    public SimpleHostsKeyEntry(Marker marker, String hosts, HostKeyType keyType, PublicKey publicKey) {
        super(marker, hosts, keyType, publicKey);
    }

    @Override
    public void setHosts(String hosts) {
        super.setHosts(hosts);
        if (hosts != null) {
            this.hostList.addAll(Collects.asSet(Strings.split(hosts, ",")));
        }
    }

    @Override
    protected boolean containsHost(String host) {
        return hostList.contains(host);
    }
}
