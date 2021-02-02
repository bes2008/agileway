package com.jn.agileway.ssh.client;

/**
 * 其实就是 connection
 */
public interface Connection {
    String getId();

    void setId();

    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    boolean isClosed();

    boolean isConnected();

    Channel openChannel(String channelType);

}
