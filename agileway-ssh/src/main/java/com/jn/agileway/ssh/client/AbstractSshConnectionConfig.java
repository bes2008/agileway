package com.jn.agileway.ssh.client;

import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;

import java.util.Map;

public class AbstractSshConnectionConfig implements SshConnectionConfig {
    /**
     * ssh 服务端 主机
     */
    private String host;
    /**
     * ssh 服务端 端口
     */
    private int port = 22;

    /**
     * 连接创建时，绑定的本地主机
     */
    private String localHost;

    /**
     * 连接创建时，绑定的本地端口
     */
    private int localPort;

    /**
     * 用户名称
     */
    private String user;

    /**
     * 用户密码
     * <p>
     * 认证时，采用密码校验时，需要设置
     */
    private String password;

    /**
     * 认证时，采用public key 方式时，需要设置
     */
    private String privateKeyfilePath;

    /**
     * 认证时，采用public key 方式时，并且 private-key 文件有密码时，需要设置
     */
    private String privateKeyfilePassphrase;

    /**
     * 由分号分割的路径集合
     */
    private String knownHostsPath = "${user.home}/.ssh/known_hosts";

    private Map<String, Object> props = Collects.emptyHashMap();

    @Override
    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getLocalHost() {
        return this.localHost;
    }

    public void setLocalHost(String localHost) {
        this.localHost = localHost;
    }

    @Override
    public int getLocalPort() {
        return this.localPort;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    @Override
    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPrivateKeyfilePath() {
        return this.privateKeyfilePath;
    }

    public void setPrivateKeyfilePath(String privateKeyfilePath) {
        this.privateKeyfilePath = privateKeyfilePath;
    }


    @Override
    public String getPrivateKeyfilePassphrase() {
        return this.privateKeyfilePassphrase;
    }

    public void setPrivateKeyfilePassphrase(String privateKeyfilePassphrase) {
        this.privateKeyfilePassphrase = privateKeyfilePassphrase;
    }

    @Override
    public String getKnownHostsPath() {
        return knownHostsPath;
    }

    public void setKnownHostsPath(String knownHostsPath) {
        this.knownHostsPath = knownHostsPath;
    }

    public void setProps(Map<String, Object> props) {
        if (props != null) {
            this.props = props;
        }
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public Object getProperty(String property) {
        return this.props.get(property);
    }

    public void setProperty(String property, Object value) {
        this.props.put(property, value);
    }

    @Override
    public boolean hasProperty(String property) {
        if (Strings.isEmpty(property)) {
            return false;
        }
        return this.props.containsKey(property);
    }
}
