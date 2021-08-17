package com.jn.agileway.ssh.client.impl.synergy;

import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.sshtools.client.SessionChannelNG;
import com.sshtools.client.SshClient;
import com.sshtools.common.publickey.SshKeyUtils;
import com.sshtools.common.ssh.components.SshKeyPair;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;

public class SynergyConnection extends AbstractSshConnection<SynergyConnectionConfig> {
    private SshClient client;

    @Override
    protected void doClose() throws IOException {
        client.close();
    }

    @Override
    public void connect(String host, int port) throws SshException {
        sshConfig.setHost(host);
        sshConfig.setPort(port);
    }

    @Override
    public void connect(InetAddress host, int port) throws SshException {
        sshConfig.setHost(host.getHostName());
        sshConfig.setPort(port);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SshException {
        sshConfig.setHost(host.getHostName());
        sshConfig.setPort(port);
        sshConfig.setLocalHost(localAddr.getHostName());
        sshConfig.setLocalPort(localPort);
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        try {
            client = new SshClient(sshConfig.getHost(), sshConfig.getPort(), user, password.toCharArray());
            return true;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPublicKey(String user, byte[] pemPrivateKey, String passphrase) throws SshException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pemPrivateKey);
            SshKeyPair keyPair = SshKeyUtils.getPrivateKey(inputStream, passphrase);
            client = new SshClient(sshConfig.getHost(), sshConfig.getPort(), user, keyPair);
            return true;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public SessionedChannel openSession() throws SshException {
        SessionChannelNG channelNG = null;
        try {
            channelNG = client.openSessionChannel();
            return new SynergySessionedChannel( channelNG);
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public ForwardingClient forwardingClient() {
        return new SynergyForwardingClient(this);
    }

    @Override
    public SftpSession openSftpSession() throws SshException {
        return null;
    }

    SshClient getClient() {
        return this.client;
    }

}
