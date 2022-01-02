package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;

import java.security.PublicKey;

public abstract class AbstractHostsKeyEntry implements HostsKeyEntry {

    private Marker marker;
    private String hosts;
    private HostKeyType keyType;
    private PublicKey publicKey;


    protected AbstractHostsKeyEntry() {

    }

    protected AbstractHostsKeyEntry(Marker marker, String hosts, HostKeyType keyType, PublicKey publicKey) {
        setMarker(marker);
        setHosts(hosts);
        setKeyType(keyType);
        setPublicKey(publicKey);
    }

    @Override
    public boolean applicableTo(String host, String type) {
        if (!isValid()) {
            return false;
        }
        if (!Objs.equals(keyType.getName(), type)) {
            return false;
        }
        return containsHost(host);
    }

    protected abstract boolean containsHost(String host);

    @Override
    public boolean verify(PublicKey key) {
        return key.equals(this.publicKey) && marker != Marker.REVOKED;
    }

    public boolean isValid() {
        return Emptys.isNoneEmpty(keyType, hosts, publicKey);
    }

    @Override
    public HostKeyType getKeyType() {
        return keyType;
    }

    @Override
    public String getHosts() {
        return hosts;
    }


    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public void setKeyType(HostKeyType keyType) {
        this.keyType = keyType;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public PublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public String getLine() {
        if (isValid()) {
            if (getMarker() == null) {
                return StringTemplates.formatWithPlaceholder("{} {} {}", getHosts(), getKeyType(), getPublicKey());
            } else {
                return StringTemplates.formatWithPlaceholder("{} {} {} {}", getMarker().getName(), getHosts(), getKeyType(), getPublicKey());
            }
        } else {
            return "invalid";
        }
    }

    @Override
    public String toString() {
        return getLine();
    }
}
