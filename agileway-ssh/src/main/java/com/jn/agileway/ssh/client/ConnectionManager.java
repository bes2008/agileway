package com.jn.agileway.ssh.client;

public interface ConnectionManager {
    /**
     * 连接并认证
     * @param sessionDefinition
     * @return
     */
    Connection connect(SessionDefinition sessionDefinition);

    void disconnect(Connection connection);
}
