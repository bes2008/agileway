package com.jn.agileway.ssh.client.impl.j2ssh;

import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.agileway.ssh.client.impl.j2ssh.transport.hostkey.verifier.ToJ2sshHostKeyVerifier;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Loggers;
import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class J2sshConnection extends AbstractSshConnection<J2sshConnectionConfig> {
    private static Logger logger = Loggers.getLogger(J2sshConnection.class);
    private SshClient sshClient = new SshClient();

    public J2sshConnection() {
    }


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
            sshClient.connect(host.getHostName(), port, new ToJ2sshHostKeyVerifier(this.hostKeyVerifier));
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        PasswordAuthenticationClient authenticationClient = new PasswordAuthenticationClient();
        authenticationClient.setPassword(password);
        authenticationClient.setUsername(user);
        try {
            int result = sshClient.authenticate(authenticationClient);
            setStatus(SshConnectionStatus.CONNECTED);
            return true;
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, byte[] pemPrivateKey, String passphrase) throws SshException {
        PublicKeyAuthenticationClient authenticationClient = new PublicKeyAuthenticationClient();

        try {
            SshPrivateKeyFile privateKeyFile = SshPrivateKeyFile.parse(pemPrivateKey);
            SshPrivateKey privateKey = privateKeyFile.toPrivateKey(passphrase);

            authenticationClient.setUsername(user);
            authenticationClient.setKey(privateKey);

            int result = sshClient.authenticate(authenticationClient);
            setStatus(SshConnectionStatus.CONNECTED);
            return true;
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    @Override
    public SessionedChannel openSession() throws SshException {
        Preconditions.checkState(getStatus() == SshConnectionStatus.CONNECTED, "ssh not connected");
        try {
            SessionChannelClient sessionChannel = sshClient.openSessionChannel();
            return new J2sshSessionedChannel(sessionChannel);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingClient forwardingClient() {
        return new J2sshForwardingClient(this);
    }

    @Override
    protected void doClose() throws IOException {
        if (sshClient != null) {
            sshClient.disconnect();
        }
    }

    @Override
    public SftpSession openSftpSession() throws SshException {
        return null;
    }

    SshClient getDelegate(){
        return this.sshClient;
    }
}
