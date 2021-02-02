package com.jn.agileway.ssh.client;

public interface Channel {
    String getType();

    boolean isStarted();

    boolean isStopped();

    void stop();

}
