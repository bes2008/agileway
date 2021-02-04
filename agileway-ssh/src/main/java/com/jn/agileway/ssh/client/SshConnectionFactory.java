package com.jn.agileway.ssh.client;

import com.jn.langx.factory.Factory;

public interface SshConnectionFactory<CONF extends SshConnectionConfig> extends Factory<CONF, SshConnection> {
    /**
     * 获取一个认证通过的，可以直接使用的Connection
     *
     * @param sshConfig
     * @return
     */
    @Override
    SshConnection get(CONF sshConfig);

}
