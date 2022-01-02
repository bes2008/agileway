package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import java.io.Serializable;

public interface HostsKeyEntry extends Serializable {
    /**
     * 判断是否可用于指定的 host, key algorithm
     * @param host
     * @param keyType
     * @return
     */
    boolean applicableTo(String host, String keyType);

    /**
     * 进行验证
     * @param key 可选类型有3种： byte[], byte[]的base64, java.security.PublicKey
     * @return
     */
    boolean verify(Object key);

    boolean isValid();

    Marker getMarker();

    String getKeyType();

    String getHosts();

    /**
     * base64 格式的 public key，同时也是 known_hosts 中完全一样
     * @return
     */
    Object getPublicKey();
    byte[] getPublicKeyBytes();
    String getPublicKeyBase64();

    String getLine();
}
