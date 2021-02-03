package com.jn.agileway.ssh.client.impl.sshj;

import com.jn.agileway.ssh.client.SshConfig;
import com.jn.langx.util.reflect.Reflects;

public class SshjConfig implements SshConfig {

    @Override
    public String getHost() {
        return null;
    }


    @Override
    public int getPort() {
        return 0;
    }


    @Override
    public String getLocalHost() {
        return null;
    }


    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public String getConnectionClass() {
        return Reflects.getFQNClassName(SshjConnection.class);
    }

    @Override
    public String getUser() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }
}
