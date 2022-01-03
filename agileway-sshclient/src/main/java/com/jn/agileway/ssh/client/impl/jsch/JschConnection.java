package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.SshConnectionStatus;
import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.channel.forwarding.ForwardingClient;
import com.jn.agileway.ssh.client.impl.jsch.authc.PasswordUserInfo;
import com.jn.agileway.ssh.client.impl.jsch.sftp.JschSftpSession;
import com.jn.agileway.ssh.client.sftp.SftpSession;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.MapAccessor;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetAddress;

/**
 * 基于 JSch 的实现。
 */
public class JschConnection extends AbstractSshConnection<JschConnectionConfig> {
    private Logger logger = Loggers.getLogger(JschConnection.class);
    private JSch jsch;
    private Session delegate;

    public JschConnection() {
    }

    public void setJsch(JSch jsch) {
        if (jsch != null) {
            this.jsch = jsch;
        }
    }

    /**
     * 由于jsch 的 session.connect 方法内部完成了 连接创建、身份认证两个过程。
     * <p>
     * 所以这里在调用connect时，并不进行实际的connect操作，延迟到 authenticate 方法里
     *
     * @param host
     * @param port
     * @throws IOException
     */
    @Override
    public void connect(String host, int port) throws SshException {
        sshConfig.setHost(host);
        sshConfig.setPort(port);
    }

    @Override
    public void connect(InetAddress host, int port) throws SshException {
        connect(host.getHostName(), port);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SshException {
        connect(host, port);
    }

    private int getConnectTimeout() {
        MapAccessor mapAccessor = new MapAccessor(sshConfig.getProps());
        int connectTimeout = mapAccessor.getInteger("ConnectTimeout", 0);
        if (connectTimeout < 0) {
            connectTimeout = 0;
        }
        return connectTimeout;
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws SshException {
        if (!isConnected()) {
            sshConfig.setUser(user);
            sshConfig.setPassword(password);
            if (delegate != null) {
                delegate.disconnect();
                delegate = null;
            }

            try {
                // 设置认证方式的顺序，让密码方式认证优先执行
                JSch.setConfig("PreferredAuthentications", "password,publickey,gssapi-with-mic,keyboard-interactive");
                delegate = jsch.getSession(user, getHost(), getPort());
                delegate.setPassword(password);
                /*
                PasswordUserInfo userInfo = new PasswordUserInfo();
                userInfo.setPassword(password);
                delegate.setUserInfo(userInfo);
                 */
                delegate.connect();
                // delegate.connect(getConnectTimeout());
                setStatus(SshConnectionStatus.CONNECTED);
                return true;
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
                if (delegate != null) {
                    delegate.disconnect();
                    delegate = null;
                }
            }
        }
        return true;
    }


    @Override
    public boolean authenticateWithPublicKey(String user, byte[] pemPrivateKey, String passphrase) throws SshException {
        if (!isConnected()) {
            sshConfig.setUser(user);

            if (delegate != null) {
                delegate.disconnect();
                delegate = null;
            }
            try {
                delegate = jsch.getSession(user, getHost(), getPort());

                if (Strings.isNotBlank(passphrase)) {
                    PasswordUserInfo userInfo = new PasswordUserInfo();
                    userInfo.setPassphrase(passphrase);
                    delegate.setUserInfo(userInfo);
                }
                delegate.connect(getConnectTimeout());
                setStatus(SshConnectionStatus.CONNECTED);
                return true;
            } catch (Throwable ex) {
                if (delegate != null) {
                    delegate.disconnect();
                    delegate = null;
                }
                return false;
            }
        }
        return true;
    }


    @Override
    public SessionedChannel openSession() throws SshException {
        Preconditions.checkState(getStatus() == SshConnectionStatus.CONNECTED, "ssh not connected");
        Preconditions.checkNotNull(delegate != null && delegate.isConnected());
        return new JschSessionedChannel(delegate);
    }

    @Override
    public ForwardingClient forwardingClient() {
        return new JschForwardingClient(this);
    }

    @Override
    protected void doClose() throws IOException {
        if (delegate != null) {
            delegate.disconnect();
        }
    }

    @Override
    public SftpSession openSftpSession() throws SshException {
        try {
            ChannelSftp channel = (ChannelSftp) this.delegate.openChannel("sftp");
            channel.connect();
            JschSftpSession session = new JschSftpSession(channel);
            session.setSshConnection(this);
            return session;
        } catch (JSchException ex) {
            throw new SshException(ex);
        }
    }

    Session delegate() {
        return delegate;
    }

}
