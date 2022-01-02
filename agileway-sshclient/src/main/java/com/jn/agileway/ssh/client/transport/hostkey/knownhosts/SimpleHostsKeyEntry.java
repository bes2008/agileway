package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.util.Set;

public class SimpleHostsKeyEntry extends AbstractHostsKeyEntry {
    private transient Set<String> hostList;

    public SimpleHostsKeyEntry() {
    }

    public SimpleHostsKeyEntry(String hosts, String keyType, Object publicKey) {
        this(null, hosts, keyType, publicKey);
    }

    public SimpleHostsKeyEntry(Marker marker, String hosts, String keyType, Object publicKey) {
        super(marker, hosts, keyType, publicKey);
    }

    @Override
    public void setHosts(String hosts) {
        if (this.hostList == null) {
            this.hostList = Collects.emptyHashSet();
        }
        super.setHosts(hosts);
        if (hosts != null) {
            this.hostList.addAll(Collects.asSet(Strings.split(hosts, ",")));
        }
    }

    @Override
    protected boolean containsHost(String host) {
        if (this.hostList == null) {
            return this.getHosts().contains(host);
        }
        return hostList.contains(host);
    }
}
