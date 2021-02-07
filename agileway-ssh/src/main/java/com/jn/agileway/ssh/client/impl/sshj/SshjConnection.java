package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import net.schmizz.sshj.SSHClient;

import java.io.IOException;
import java.net.InetAddress;

public class SshjConnection extends AbstractSshConnection<SshjConnectionConfig> {
    private SSHClient sshClient;

    private void makeSureSshClient() {
        if (sshClient == null) {
            sshClient = new SSHClient();
        }
    }

    @Override
    public void connect(String host, int port) throws IOException {
        if (!sshClient.isConnected()) {
            sshClient.connect(host, port);
        }
    }

    @Override
    public void connect(InetAddress host, int port) throws IOException {
        if (!sshClient.isConnected()) {
            sshClient.connect(host, port);
        }
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws IOException {
        if (!sshClient.isConnected()) {
            sshClient.connect(host, port, localAddr, localPort);
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws IOException {
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String password) throws IOException {
        return false;
    }


    @Override
    public SessionedChannel openSession() throws IOException{
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
