package com.jn.agileway.ssh.client;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.NotEmpty;
import com.jn.langx.annotation.Nullable;

import java.io.File;
import java.util.List;
import java.util.Map;

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

    String getKnownHostsPath();

    void setProperty(String property, Object value);

    Object getProperty(String property);

    boolean hasProperty(String property);
}
