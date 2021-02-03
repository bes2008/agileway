package com.jn.agileway.ssh.client;

public interface ConnectionManager {
    Connection connect(String host, int port);

    void disconnect(Connection connection);
}
