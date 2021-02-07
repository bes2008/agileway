package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.Connection;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionChannel;

import java.io.IOException;
import java.net.InetAddress;

public class Ssh2Connection extends AbstractSshConnection<Ssh2ConnectionConfig> {

    private Connection conn;
    private boolean closed = false;
    private boolean connected = false;

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
    public boolean authenticateWithPassword(String user, String password) throws IOException {
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws IOException {
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
