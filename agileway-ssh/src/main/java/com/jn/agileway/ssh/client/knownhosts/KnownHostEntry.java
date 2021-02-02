package com.jn.agileway.ssh.client.knownhosts;

import com.jn.langx.util.enums.Enums;

public class KnownHostEntry {
    private String hostName;
    private KnownHostType keyType;
    private String publicKey;

    public KnownHostEntry(String hostName, String keyType, String publicKey) {
        this(hostName, Enums.ofName(KnownHostType.class, keyType), publicKey);
    }

    public KnownHostEntry(String hostName, KnownHostType keyType, String publicKey) {
        setHostName(hostName);
        setKeyType(keyType);
        setPublicKey(publicKey);
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public KnownHostType getKeyType() {
        return keyType;
    }

    public void setKeyType(KnownHostType keyType) {
        this.keyType = keyType;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
