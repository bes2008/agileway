package com.jn.agileway.ssh.client.impl.ganymedssh2;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.agileway.ssh.client.impl.ganymedssh2.sftp.Ssh2SftpSession;
import com.jn.agileway.ssh.client.impl.ganymedssh2.verifier.ToSsh2HostKeyVerifierAdapter;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.io.Charsets;
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
    public boolean authenticateWithPublicKey(String user, byte[] pemPrivateKey, String passphrase) throws SshException {
        Preconditions.checkState(delegate != null);
        try {
            return delegate.authenticateWithPublicKey(user, new String(pemPrivateKey, Charsets.UTF_8).toCharArray(), passphrase);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }


    @Override
    public SessionedChannel openSession() throws SshException {
        Preconditions.checkNotNull(delegate);
        Preconditions.checkState(getStatus() == SshConnectionStatus.CONNECTED, "ssh not connected");
        try {
            Session session = delegate.openSession();
            Ssh2SessionedChannel channel = new Ssh2SessionedChannel(session);
            return channel;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingClient forwardingClient() {
        return new Ssh2ForwardingClient(this);
    }

    @Override
    protected void doClose() throws IOException {
        if (this.delegate != null) {
            this.delegate.close();
        }
    }

    @Override
    public SftpSession openSftpSession() throws SshException {
        try {
            SFTPv3Client sftpClient = new SFTPv3Client(this.delegate);
            Ssh2SftpSession session = new Ssh2SftpSession(sftpClient);
            session.setSshConnection(this);
            return session;
        } catch (Throwable ex) {
            throw new SshException(ex.getMessage(), ex);
        }
    }

    Connection getDelegate() {
        return delegate;
    }

}
