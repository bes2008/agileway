package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jn.agileway.ssh.client.AbstractSshConnection;
import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionChannel;
import com.jn.agileway.ssh.client.impl.jsch.authc.PasswordUserInfo;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;

/**
 * 基于 JSch 的实现。
 */
public class JschConnection extends AbstractSshConnection<JschConnectionConfig> {
    private Logger logger = LoggerFactory.getLogger(JschConnection.class);
    private JSch jsch;
    private Session delegate;

    public JschConnection() {
        setJsch(new JSch());
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
    public void connect(String host, int port) throws IOException {
        sshConfig.setHost(host);
        sshConfig.setPort(port);
    }

    @Override
    public void connect(InetAddress host, int port) throws IOException {
        connect(host.getHostName(), port);
    }

    @Override
    public void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws IOException {
        connect(host, port);
    }

    @Override
    public boolean isClosed() {
        if (delegate == null) {
            return false;
        }
        return delegate.isConnected();
    }

    @Override
    public boolean isConnected() {
        return delegate != null && delegate.isConnected();
    }

    @Override
    public boolean authenticateWithPassword(String user, String password) throws IOException {
        if (!isConnected()) {
            sshConfig.setUser(user);
            sshConfig.setPassword(password);
            if (delegate != null) {
                delegate.disconnect();
                delegate = null;
            }

            try {
                delegate = jsch.getSession(user, getHost(), getPort());
                PasswordUserInfo userInfo = new PasswordUserInfo();
                userInfo.setPassword(password);
                delegate.setUserInfo(userInfo);
                delegate.connect();
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
    public boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws IOException {
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
                delegate.connect();

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
    public SessionChannel openSession() {
        Preconditions.checkNotNull(delegate != null && delegate.isConnected());
        return new JschSessionChannel(delegate);
    }

    @Override
    public Channel openForwardChannel() {
        return null;
    }

    @Override
    public void close() throws IOException {
        if (delegate != null) {
            delegate.disconnect();
        }
    }
}
