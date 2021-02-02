package com.jn.agileway.ssh.client;

public interface Session {
    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    void connect();

    boolean isConnected();
}
