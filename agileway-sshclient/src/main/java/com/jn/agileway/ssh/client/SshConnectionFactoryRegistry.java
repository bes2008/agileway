package com.jn.agileway.ssh.client;

import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class SshConnectionFactoryRegistry extends AbstractInitializable implements Registry<String, SshConnectionFactory> {
    private ConcurrentHashMap<String, SshConnectionFactory> registry = new ConcurrentHashMap<String, SshConnectionFactory>();
    private static final Logger logger = LoggerFactory.getLogger(SshConnectionFactoryRegistry.class);
    private static final Map<String, SshConnectionFactory> preinstall = new LinkedHashMap<String, SshConnectionFactory>();

    static {

        Iterator<SshConnectionFactory> iterator = ServiceLoader.load(SshConnectionFactory.class).iterator();

        while (iterator.hasNext()) {
            try {
                SshConnectionFactory sshConnectionFactory = iterator.next();
                preinstall.put(sshConnectionFactory.getName(), sshConnectionFactory);
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
            }
        }

        if (preinstall.size() == 0) {
            logger.warn("Can't find any valid ssh-client library, the recommend ssh client libraries: net.schmizz:sshj, com.trilead:trilead-ssh2, com.airlenet.yang:ganymed-ssh2, com.jcraft:jsch");
        }
    }

    public SshConnectionFactoryRegistry() {
        init();
    }

    @Override
    public void register(SshConnectionFactory sshConnectionFactory) {
        register(sshConnectionFactory.getName(), sshConnectionFactory);
    }

    @Override
    public void register(String key, SshConnectionFactory sshConnectionFactory) {
        registry.put(key, sshConnectionFactory);
    }

    @Override
    public SshConnectionFactory get(String name) {
        return registry.get(name);
    }


    @Override
    protected void doInit() throws InitializationException {
        registry.putAll(preinstall);
    }

    public SshConnectionFactory getDefault() {
        Preconditions.checkNotEmpty(registry);
        String first = Pipeline.of(Collects.<String>asIterable(registry.keys())).findFirst();
        return get(first);
    }
}
