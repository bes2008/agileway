package com.jn.agileway.ssh.client;

import com.jn.agileway.ssh.client.transport.hostkey.StrictHostKeyChecking;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;


public interface SshConnectionConfig {

    /**
     * @return 服务端地址
     */
    @NonNull
    String getHost();

    void setHost(String host);

    /**
     * @return 服务端端口
     */
    @NotEmpty
    int getPort();

    void setPort(int port);

    /**
     * @return 本地地址
     */

    @Nullable
    String getLocalHost();

    void setLocalHost(String localHost);

    /**
     * @return 本地端口
     */
    @Nullable
    int getLocalPort();

    void setLocalPort(int localPort);

    /**
     * 获取用户名称
     *
     * @return username
     */
    @NotEmpty
    String getUser();

    void setUser(String user);

    /**
     * @return 用户的密码
     */
    @Nullable
    String getPassword();

    void setPassword(String password);

    /**
     * 私钥文件在本地的路径
     */
    @Nullable
    String getPrivateKeyfilePath();

    /**
     * 私钥文件在本地的路径
     */
    String getPrivateKeyfilePassphrase();

    /**
     * 当设置为true 且没有自定义 host key verifier时，会自动根据 known_hosts文件进行验证
     * @return
     */
    StrictHostKeyChecking getStrictHostKeyChecking();

    void setStrictHostKeyChecking(StrictHostKeyChecking checking);

    String getKnownHostsPath();

    void setKnownHostsPath(String knownHostsPath);

    HostKeyVerifier getHostKeyVerifier();
    void setHostKeyVerifier(HostKeyVerifier hostKeyVerifier);

    void setProperty(String property, Object value);

    Object getProperty(String property);

    boolean hasProperty(String property);
}
