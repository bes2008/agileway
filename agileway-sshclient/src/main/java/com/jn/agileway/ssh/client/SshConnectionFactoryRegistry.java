package com.jn.agileway.ssh.client;

import com.jn.langx.annotation.OnClasses;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.registry.Registry;
import com.jn.langx.util.ClassLoaders;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.reflect.Reflects;
import com.jn.langx.util.reflect.annotation.OnClassesConditions;
import com.jn.langx.util.spi.AllPresentServiceProvider;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public class SshConnectionFactoryRegistry extends AbstractInitializable implements Registry<String, SshConnectionFactory> {
    private ConcurrentHashMap<String, SshConnectionFactory> registry = new ConcurrentHashMap<String, SshConnectionFactory>();
    private static final Logger logger = Loggers.getLogger(SshConnectionFactoryRegistry.class);
    private static final Map<String, SshConnectionFactory> preinstall = new LinkedHashMap<String, SshConnectionFactory>();

    static {

        Iterator<SshConnectionFactory> iterator = ServiceLoader.load(SshConnectionFactory.class).iterator();

        while (iterator.hasNext()) {
            try {
                SshConnectionFactory sshConnectionFactory = iterator.next();
                if (OnClassesConditions.allPresent(sshConnectionFactory.getClass(), true)) {
                    preinstall.put(sshConnectionFactory.getName(), sshConnectionFactory);
                }
            } catch (Throwable ex) {
                logger.error(ex.getMessage());
            }
        }

        if (preinstall.isEmpty()) {
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

    private static boolean checkSshConnectionFactory(final SshConnectionFactory factory) {
        return OnClassesConditions.allPresent(factory.getClass(), true);
    }

    @Override
    public void register(String key, SshConnectionFactory sshConnectionFactory) {
        if (sshConnectionFactory != null) {
            registry.put(key, sshConnectionFactory);
        }
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
