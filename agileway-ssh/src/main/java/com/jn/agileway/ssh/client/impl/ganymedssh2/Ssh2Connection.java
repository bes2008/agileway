package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.net.Nets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Ssh2Connection extends AbstractSshConnection<Ssh2ConnectionConfig> {

    private Connection delegate;

    @Override
    public void connect(String host, int port) throws IOException {
        connect(InetAddress.getByName(host), port);
    }

    @Override
    public void connect(InetAddress host, int port) throws IOException {
        connect(host, port, null, -1);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws IOException {
        if (delegate == null) {
            Socket localSocket = null;
            if (localAddr != null && Nets.isValidPort(localPort)) {
                localSocket = new Socket(localAddr, localPort);
            }
            Connection conn = new Connection(host.getHostName(), port, localSocket);
            conn.connect();
            setStatus(SshConnectionStatus.CONNECTED);
            this.delegate = conn;
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws IOException {
        Preconditions.checkState(delegate != null);
        return delegate.authenticateWithPassword(user, password);
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws IOException {
        Preconditions.checkState(delegate != null);
        return delegate.authenticateWithPublicKey(user, pemPrivateKey, passphrase);
    }


    @Override
    public SessionedChannel openSession() throws IOException{
        Session session = delegate.openSession();
        Ssh2SessionedChannel channel = new Ssh2SessionedChannel(session);
        return channel;
    }

    @Override
    public Channel openForwardChannel() {
        return null;
    }

    @Override
    public void close() throws IOException {
        setStatus(SshConnectionStatus.CLOSED);
    }
}
