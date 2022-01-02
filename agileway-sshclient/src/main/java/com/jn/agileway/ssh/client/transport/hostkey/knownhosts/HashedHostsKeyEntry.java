package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.security.Securitys;
import com.jn.langx.security.crypto.mac.HMacs;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

import java.security.PublicKey;

public class HashedHostsKeyEntry extends AbstractHostsKeyEntry {
    public static final String HOSTS_FLAG = "|1|";

    private transient byte[] salt;
    private transient String hostsHash; // base64

    public HashedHostsKeyEntry() {
    }

    public HashedHostsKeyEntry(String hosts, HostKeyType keyType, PublicKey publicKey) {
        this(null, hosts, keyType, publicKey);
    }

    public HashedHostsKeyEntry(Marker marker, String hosts, HostKeyType keyType, PublicKey publicKey) {
        super(marker, null, keyType, publicKey);
        if (hosts.startsWith(HOSTS_FLAG)) {
            final String[] hostParts = Strings.split(hosts, "\\|");
            if (hostParts.length != 3) {
                throw new SshException("Unrecognized format for hashed hostname");
            }
            salt = Base64.decodeBase64(hostParts[1]);
            hostsHash = hostParts[2];
            setHosts(getFormattedHosts());
        } else {
            setHosts(hash(hosts));
        }
    }

    private String getFormattedHosts() {
        return StringTemplates.formatWithPlaceholder("|1|{}|{}", Base64.encodeBase64String(salt), hostsHash);
    }

    private byte[] hash(byte[] hosts) {
        if (salt == null) {
            salt = newSalt();
        }
        return HMacs.hmac("HMacSHA1", salt, hosts);
    }

    private String hash(String hosts) {
        return Base64.encodeBase64String(hash(hosts.getBytes()));
    }

    private static byte[] newSalt() {
        byte[] salt = new byte[20];
        Securitys.getSecureRandom().nextBytes(salt);
        return salt;
    }

    @Override
    protected boolean containsHost(String host) {
        return Objs.equals(hash(host), hostsHash);
    }


}
