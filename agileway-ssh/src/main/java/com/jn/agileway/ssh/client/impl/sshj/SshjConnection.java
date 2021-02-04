package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshConnection;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionChannel;
import net.schmizz.sshj.SSHClient;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class SshjConnection implements SshConnection {
    private String id;
    /**
     * 服务端主机
     */
    private String host;
    /**
     * 服务端端口
     */
    private int port;

    private SSHClient sshClient;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

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
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String password) throws IOException {
        return false;
    }

    @Override
    public boolean authenticateWithPublicKey(String user, File pemFile, String password) throws IOException {
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
