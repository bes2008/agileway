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

    /**
     * @return 服务端端口
     */
    @NotEmpty
    int getPort();

    /**
     * @return 本地地址
     */

    @Nullable
    String getLocalHost();

    /**
     * @return 本地端口
     */
    @Nullable
    int getLocalPort();

    /**
     * 获取用户名称
     *
     * @return username
     */
    @NotEmpty
    String getUser();

    /**
     * @return 用户的密码
     */
    @Nullable
    String getPassword();

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

    Object getProperty(String property);

    boolean hasProperty(String property);
}
