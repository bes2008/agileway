package com.jn.agileway.ssh.client;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.Nets;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.InetAddress;

public abstract class AbstractSshConnectionFactory<CONF extends SshConnectionConfig> implements SshConnectionFactory<CONF> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public SshConnection get(CONF sshConfig) {
        return connectAndAuthenticate(sshConfig);
    }

    protected SshConnection createConnection(CONF sshConfig) {
        SshConnection connection = null;
        Class connectionClass = getDefaultConnectionClass();
        Preconditions.checkNotNull(connectionClass);
        connection = Reflects.<SshConnection>newInstance(connectionClass);
        connection.setConfig(sshConfig);
        postConstructConnection(connection, sshConfig);
        return connection;

    }

    /**
     * 1） 设置代理
     * 2） 设置 host key verifier
     *
     * @param connection
     * @param sshConfig
     */
    protected abstract void postConstructConnection(@NonNull SshConnection connection, @NonNull CONF sshConfig);

    protected abstract Class<?> getDefaultConnectionClass();

    /**
     * 创建连接并进行身份认证
     *
     * @param sshConfig
     * @return 成功则返回 connection，不成功则返回 null
     */
    protected SshConnection connectAndAuthenticate(CONF sshConfig) {
        SshConnection connection = createConnection(sshConfig);
        String host = sshConfig.getHost();
        int port = sshConfig.getPort();

        // step 1: 检查 host, port
        InetAddress remoteAddress = null;
        try {
            remoteAddress = InetAddress.getByName(host);
        } catch (Throwable ex) {
            logger.error("invalid host: {}", host);
            return null;
        }
        if (!Nets.isValidPort(port)) {
            logger.error("invalid port: {}", port);
            return null;
        }


        // step 2: 检查 localHost, localPort
        String localHost = sshConfig.getLocalHost();
        int localPort = sshConfig.getLocalPort();

        InetAddress localAddress = null;
        if (Emptys.isNotEmpty(localHost)) {
            try {
                localAddress = InetAddress.getByName(localHost);
            } catch (Throwable ex) {
                logger.warn(ex.getMessage(), ex);
            }
        }

        // step 3: connect socket to server
        try {
            if (localAddress != null && Nets.isValidPort(localPort)) {
                connection.connect(remoteAddress, port, localAddress, localPort);
            } else {
                connection.connect(host, port);
            }
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            IOs.close(connection);
            connection = null;
            return null;
        }

        if (authenticate(connection, sshConfig)) {
            return connection;
        }
        return null;
    }

    protected boolean authenticate(SshConnection connection, SshConnectionConfig sshConfig) {
        // step 1: do authc
        String user = sshConfig.getUser();
        if (Strings.isBlank(user)) {
            return false;
        }
        String password = sshConfig.getPassword();

        boolean authcSuccess = false;
        // 这里只提供两种认证方式，至于交互式方式，则是有这两种方式内部去实现

        // 使用密码认证
        if (Strings.isNotBlank(password)) {
            try {
                authcSuccess = connection.authenticateWithPassword(user, password);
            } catch (SshException ex) {
                logger.error(ex.getMessage(), ex);
                return false;
            }
        }

        // 使用 public key 认证
        if (!authcSuccess) {
            @Nullable
            String privateKeyfilePath = sshConfig.getPrivateKeyfilePath();
            if (Strings.isNotBlank(privateKeyfilePath)) {
                @Nullable
                String passphrase = sshConfig.getPrivateKeyfilePassphrase();
                File privateKeyfile = new File(privateKeyfilePath);
                if (privateKeyfile.exists()) {
                    try {
                        authcSuccess = connection.authenticateWithPublicKey(user, privateKeyfile, passphrase);
                    } catch (SshException ex) {
                        logger.error(ex.getMessage(), ex);
                        return false;
                    }
                }
            }

        }
        return authcSuccess;
    }
}
