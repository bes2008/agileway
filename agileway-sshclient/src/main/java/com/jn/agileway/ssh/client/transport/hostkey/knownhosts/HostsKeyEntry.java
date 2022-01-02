package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;

import java.io.Serializable;
import java.security.PublicKey;

public interface HostsKeyEntry extends Serializable {
    /**
     * 判断是否可用于指定的 host, key algorithm
     * @param host
     * @param keyAlgorithm
     * @return
     */
    boolean applicableTo(String host, String keyAlgorithm);

    /**
     * 进行验证
     * @param key
     * @return
     */
    boolean verify(PublicKey key);

    boolean isValid();

    Marker getMarker();

    HostKeyType getKeyType();

    String getHosts();

    /**
     * base64 格式的 public key，同时也是 known_hosts 中完全一样
     * @return
     */
    PublicKey getPublicKey();

    String getLine();
}
