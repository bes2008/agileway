package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.langx.codec.base64.Base64;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

import java.security.PublicKey;

public abstract class AbstractHostsKeyEntry implements HostsKeyEntry {

    private Marker marker;
    private String hosts;
    private String keyType;
    /**
     * 可选类型：byte[], byte[] 的 base64 String， java.security.PublicKey
     */
    private Object publicKey;


    protected AbstractHostsKeyEntry() {

    }

    protected AbstractHostsKeyEntry(Marker marker, String hosts, String keyType, Object publicKey) {
        setMarker(marker);
        setHosts(hosts);
        setKeyType(keyType);
        setPublicKey(publicKey);
    }

    @Override
    public boolean applicableTo(String host, String keyType) {
        if (!isValid()) {
            return false;
        }

        if (Strings.isNotBlank(keyType) && !Objs.equals(this.keyType, keyType)) {
            return false;
        }
        return containsHost(host);
    }

    protected abstract boolean containsHost(String host);

    @Override
    public boolean verify(Object key) {
        return Objs.deepEquals(HostsKeyEntrys.getPublicKeyBytes(this), HostsKeyEntrys.getPublicKeyBytes(this)) && marker != Marker.REVOKED;
    }

    public boolean isValid() {
        return Emptys.isNoneEmpty(keyType, hosts, publicKey);
    }

    @Override
    public String getKeyType() {
        return keyType;
    }

    @Override
    public String getHosts() {
        return hosts;
    }


    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public void setPublicKey(Object publicKey) {
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
    public Object getPublicKey() {
        return publicKey;
    }

    @Override
    public String getLine() {
        return getLine(true);
    }

    protected String getLine(boolean dumpPublicKey) {
        if (isValid()) {
            if (getMarker() == null) {
                return StringTemplates.formatWithPlaceholder("{} {}{}", getHosts(), getKeyType(), dumpPublicKey ? (" " + getPublicKeyBase64()) : "");
            } else {
                return StringTemplates.formatWithPlaceholder("{} {} {}{}", getMarker().getName(), getHosts(), getKeyType(), dumpPublicKey ? (" " + getPublicKeyBase64()) : "");
            }
        } else {
            return "invalid";
        }
    }

    public String getPublicKeyBase64() {
        if (publicKey == null) {
            return null;
        }
        if (publicKey instanceof PublicKey) {
            return Base64.encodeBase64String(((PublicKey) publicKey).getEncoded());
        }
        if (publicKey instanceof byte[]) {
            return Base64.encodeBase64String((byte[]) publicKey);
        }
        if (publicKey instanceof String) {
            return (String) publicKey;
        }
        return publicKey.toString();
    }

    @Override
    public String toString() {
        return getLine(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractHostsKeyEntry that = (AbstractHostsKeyEntry) o;
        if (!Objs.equals(this.marker, that.marker)) {
            return false;
        }
        if (!Objs.equals(this.hosts, that.hosts)) {
            return false;
        }
        if (!Objs.equals(this.keyType, that.keyType)) {
            return false;
        }
        if (!Objs.equals(HostsKeyEntrys.toPublicKeyBytes(this.publicKey), HostsKeyEntrys.toPublicKeyBytes(that.publicKey))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objs.hash(this.marker, this.hosts, this.keyType, HostsKeyEntrys.toPublicKeyBytes(this.publicKey));
    }
}
