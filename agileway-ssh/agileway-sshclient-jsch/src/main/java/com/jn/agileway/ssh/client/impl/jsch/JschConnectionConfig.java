package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jcraft.jsch.JSch;
import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.MapAccessor;

public class JschConnectionConfig extends AbstractSshConnectionConfig implements ConfigRepository.Config {
    @Override
    public String getHostname() {
        return getHost();
    }

    @Override
    public String getValue(String property) {
        Object obj = this.getProperty(property);
        if (obj == null) {
            return null;
        }

        return obj.toString();
    }

    @Override
    public String[] getValues(String property) {
        String value = getValue(property);
        if (Strings.isNotBlank(value)) {
            return Strings.split(value, ",");
        } else {
            return Emptys.EMPTY_STRINGS;
        }
    }

    public int getConnectTimeout() {
        MapAccessor mapAccessor = new MapAccessor(this.getProps());
        int connectTimeout = mapAccessor.getInteger("ConnectTimeout", 0);
        if (connectTimeout < 0) {
            connectTimeout = 0;
        }
        return connectTimeout;
    }

    public int getSocketTimeout() {
        MapAccessor mapAccessor = new MapAccessor(this.getProps());
        int socketTimeout = mapAccessor.getInteger("SocketTimeout", 60000);
        if (socketTimeout <= 0) {
            socketTimeout = 60000;
        }
        return socketTimeout;
    }

    public String getHostKeyAlgorithms(){
        MapAccessor mapAccessor = new MapAccessor(this.getProps());
        String append = mapAccessor.getString("ServerHostKey", ",ssh-rsa,ssh-dss");
        return JSch.getConfig("server_host_key")+append;
    }
}
