package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import java.security.PublicKey;

public class DefaultPublicKeyHostKeyTypeExtractor implements PublicKeyHostKeyTypeExtractor {
    @Override
    public String get(PublicKey publicKey) {
        if (publicKey == null) {
            return null;
        }
        String algorithm = publicKey.getAlgorithm();
        if ("RSA".equals(algorithm)) {
            return "ssh-rsa";
        }
        if ("DSA".equals(algorithm)) {
            return "ssh-dss";
        }
        if ("ECDSA".equals(publicKey.getAlgorithm())) {
            // 此时可能有多种，按 nistp256 曲线来
            return "ecdsa-sha2-nistp256";
        }
        return null;
    }
}
