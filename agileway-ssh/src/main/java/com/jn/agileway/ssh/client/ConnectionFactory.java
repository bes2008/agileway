package com.jn.agileway.ssh.client;

import com.jn.langx.factory.Factory;

public interface ConnectionFactory<CONF extends SshConfig> extends Factory<CONF, Connection> {
    /**
     * 获取一个认证通过的，可以直接使用的Connection
     *
     * @param sshConfig
     * @return
     */
    @Override
    Connection get(CONF sshConfig);

}
