package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.ConfigRepository;
import com.jn.agileway.ssh.client.AbstractSshConnectionConfig;
import com.jn.langx.util.Strings;

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
            return null;
        }
    }
}
