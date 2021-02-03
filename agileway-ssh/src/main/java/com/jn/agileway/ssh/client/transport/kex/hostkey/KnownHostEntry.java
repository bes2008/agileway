package com.jn.agileway.ssh.client.transport.kex.hostkey;

import com.jn.langx.util.enums.Enums;

public class KnownHostEntry {
    private String hostname;
    private KnownHostKeyType keyType;
    private String publicKey;

    public KnownHostEntry(String hostName, String keyType, String publicKey) {
        this(hostName, Enums.ofName(KnownHostKeyType.class, keyType), publicKey);
    }

    public KnownHostEntry(String hostName, KnownHostKeyType keyType, String publicKey) {
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

    public KnownHostKeyType getKeyType() {
        return keyType;
    }

    public void setKeyType(KnownHostKeyType keyType) {
        this.keyType = keyType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
