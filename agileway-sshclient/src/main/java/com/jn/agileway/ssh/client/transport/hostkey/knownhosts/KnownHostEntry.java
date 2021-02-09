package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;

import java.util.List;

public class KnownHostEntry {
    /**
     * 逗号分割的多个主机名
     */
    private List<String> hostnames;
    private HostKeyType keyType;
    private String publicKey;

    public KnownHostEntry(String hostName, String keyType, String publicKey) {
        this(hostName, Enums.ofName(HostKeyType.class, keyType), publicKey);
    }

    public KnownHostEntry(String hostName, HostKeyType keyType, String publicKey) {
        setHostnames(hostName);
        setKeyType(keyType);
        setPublicKey(publicKey);
    }

    public List<String> getHostnames() {
        return hostnames;
    }

    public String getHostnamesString() {
        return Strings.join(",", hostnames);
    }

    public void setHostnames(String hostname) {
        setHostnames(Collects.newArrayList(Strings.split(hostname, ",")));
    }

    public void setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
    }

    public HostKeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(HostKeyType keyType) {
        this.keyType = keyType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
