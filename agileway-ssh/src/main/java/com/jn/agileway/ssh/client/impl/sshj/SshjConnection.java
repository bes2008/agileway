package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.langx.util.net.Nets;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.userauth.UserAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SshjConnection extends AbstractSshConnection<SshjConnectionConfig> {
    private Logger logger = LoggerFactory.getLogger(SshjConnection.class);
    private SSHClient sshClient;

    private void makeSureSshClient() {
        if (sshClient == null) {
            sshClient = new SSHClient();
        }
    }

    @Override
    public void connect(String host, int port)  throws SshException{
        try {
            connect(InetAddress.getByName(host), port);
        }catch (UnknownHostException ex){
            throw new SshException(ex);
        }
    }

    @Override
    public void connect(InetAddress host, int port)  throws SshException {
        connect(host, port, null, -1);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort)  throws SshException {
        try {
            makeSureSshClient();
            if (!sshClient.isConnected()) {
                if (localAddr == null || !Nets.isValidPort(localPort)) {
                    sshClient.connect(host, port);
                } else {
                    sshClient.connect(host, port, localAddr, localPort);
                }
            }
        }catch (Throwable ex){
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        try {
            sshClient.authPassword(user, password);
            return true;
        } catch (UserAuthException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (Throwable ex) {
            throw new SshException(ex);
        }
    }

    @Override
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String password) throws SshException {
        return false;
    }


    @Override
    public SessionedChannel openSession() throws SshException {
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
