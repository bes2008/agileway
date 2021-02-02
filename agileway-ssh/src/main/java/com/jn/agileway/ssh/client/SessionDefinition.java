package com.jn.agileway.ssh.client;


import com.jn.langx.configuration.Configuration;

public interface SessionDefinition extends Configuration {
    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);
}
