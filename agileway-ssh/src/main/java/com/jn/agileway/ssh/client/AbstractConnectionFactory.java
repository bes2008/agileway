package com.jn.agileway.ssh.client;

import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.net.Nets;
import com.jn.langx.util.reflect.Reflects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public abstract class AbstractConnectionFactory<C extends SshConfig> implements ConnectionFactory<C> {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Connection get(C sshConfig) {
        return connectAndAuthc(sshConfig);
    }

    protected Connection createConnection(C sshConfig) {
        Connection connection = null;
        Class connectionClass = getConnectionClass(sshConfig);
        if (connectionClass != null) {
            connection = Reflects.<Connection>newInstance(connectionClass);
        }
        return connection;

    }

    private Class getConnectionClass(C sshConfig) {
        String connectionClass = sshConfig.getConnectionClass();
        Class connectionClazz = getDefaultConnectionClass();
        if (Strings.isNotBlank(connectionClass)) {
            if (ClassLoaders.hasClass(connectionClass, this.getClass().getClassLoader())) {
                try {
                    connectionClazz = ClassLoaders.loadClass(connectionClass, this.getClass().getClassLoader());
                } catch (Throwable ex) {
                    // ignore it
                }
            } else {
                logger.warn("Can't find the ssh connection class: {}", connectionClass);
            }
        }
        return connectionClazz;
    }

    protected abstract Class getDefaultConnectionClass();

    /**
     * 创建连接并进行身份认证
     *
     * @param sshConfig
     * @return 成功则返回 connection，不成功则返回 null
     */
    protected Connection connectAndAuthc(C sshConfig) {
        Connection connection = createConnection(sshConfig);
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
        try {
            localAddress = InetAddress.getByName(localHost);
        } catch (Throwable ex) {
            logger.warn(ex.getMessage(), ex);
        }

        // step 3: connect socket to server
        try {
            if (localAddress != null && Nets.isValidPort(localPort)) {
                connection.connect(remoteAddress, port, localAddress, localPort);
            } else {
                connection.connect(host, port);
            }
        } catch (Throwable ex) {
            IOs.close(connection);
            connection = null;
            return null;
        }


        return connection;
    }

    protected boolean authenticate(Connection connection, SshConfig sshConfig) {
        // step 1: do authc
        String user = sshConfig.getUser();
        if (Strings.isBlank(user)) {
            return false;
        }
        String password = sshConfig.getPassword();

        boolean authcSuccess = false;

        // 使用密码认证
        if (Strings.isNotBlank(password)) {
            try {
                authcSuccess = connection.authenticateWithPassword(user, password);
            } catch (IOException ex) {
                return false;
            }
        }

        // 使用 public key 认证
        if (!authcSuccess) {
            String privateKeyfilePath = sshConfig.getPrivateKeyfilePath();
            String passphrase = sshConfig.getPrivateKeyfilePassphrase();
            File privateKeyfile = new File(privateKeyfilePath);
            if (privateKeyfile.exists()) {
                try {
                    authcSuccess = connection.authenticateWithPublicKey(user, privateKeyfile, passphrase);
                } catch (IOException ex) {
                    return false;
                }
            }

        }

        // 交互式方式获取 密码、或者 public key
        return authcSuccess;
    }
}
