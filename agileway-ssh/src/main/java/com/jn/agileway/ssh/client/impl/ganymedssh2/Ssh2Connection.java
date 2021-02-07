package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.impl.ganymedssh2.verifier.ToSsh2HostKeyVerifierAdapter;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.net.Nets;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Ssh2Connection extends AbstractSshConnection<Ssh2ConnectionConfig> {

    private Connection delegate;

    @Override
    public void connect(String host, int port) throws SshException {
        try {
            connect(InetAddress.getByName(host), port);
        } catch (UnknownHostException ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public void connect(InetAddress host, int port) throws SshException {
        connect(host, port, null, -1);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SshException {
        try {
            if (delegate == null) {
                Socket localSocket = null;
                if (localAddr != null && Nets.isValidPort(localPort)) {
                    localSocket = new Socket(localAddr, localPort);
                }
                Connection conn = new Connection(host.getHostName(), port, localSocket);
                if (this.hostKeyVerifier.isEmpty()) {
                    conn.connect();
                } else {
                    conn.connect(new ToSsh2HostKeyVerifierAdapter(this.hostKeyVerifier));
                }
                setStatus(SshConnectionStatus.CONNECTED);
                this.delegate = conn;
            }
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        Preconditions.checkState(delegate != null);
        try {
            return delegate.authenticateWithPassword(user, password);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws SshException {
        Preconditions.checkState(delegate != null);
        try {
            return delegate.authenticateWithPublicKey(user, pemPrivateKey, passphrase);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }


    @Override
    public SessionedChannel openSession() throws SshException {
        Preconditions.checkNotNull(delegate);
        try {
            Session session = delegate.openSession();
            Ssh2SessionedChannel channel = new Ssh2SessionedChannel(session);
            return channel;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public Channel openForwardChannel() {
        return null;
    }


    @Override
    protected void doClose() throws IOException {
        this.delegate.close();
    }
}
