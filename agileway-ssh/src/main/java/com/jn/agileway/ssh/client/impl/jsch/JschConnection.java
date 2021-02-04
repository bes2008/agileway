package com.jn.agileway.ssh.client.impl.jsch;

import com.jn.agileway.ssh.client.Connection;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionChannel;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class JschConnection implements Connection<JschConfig> {
    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public String getHost() {
        return null;
    }

    @Override
    public JschConfig getConfig() {
        return null;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public void connect(String host, int port) throws IOException {

    }

    @Override
    public void connect(InetAddress host, int port) throws IOException {

    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws IOException {

    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws IOException {
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws IOException {
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, File pemFile, String passphrase) throws IOException {
        return false;
    }

    @Override
    public SessionChannel openSession() {
        return null;
    }

    @Override
    public Channel openForwardChannel() {
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
