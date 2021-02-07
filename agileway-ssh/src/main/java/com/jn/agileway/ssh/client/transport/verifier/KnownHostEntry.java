package com.jn.agileway.ssh.client.transport.verifier;

import com.jn.langx.util.enums.Enums;

public class KnownHostEntry {
    private String hostname;
    private HostKeyType keyType;
    private String publicKey;

    public KnownHostEntry(String hostName, String keyType, String publicKey) {
        this(hostName, Enums.ofName(HostKeyType.class, keyType), publicKey);
    }

    public KnownHostEntry(String hostName, HostKeyType keyType, String publicKey) {
        setHostname(hostName);
        setKeyType(keyType);
        setPublicKey(publicKey);
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
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
