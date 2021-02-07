package com.jn.agileway.ssh.client.transport.verifier;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.PublicKey;
import java.util.List;

public class AnyHostKeyVerifier implements HostKeyVerifier {
    private final List<HostKeyVerifier> verifiers = Collects.emptyArrayList();
    private static final Logger logger = LoggerFactory.getLogger(AnyHostKeyVerifier.class);

    @Override
    public boolean verify(final String hostname, final int port, final PublicKey key) {
        return Collects.anyMatch(verifiers, new Predicate<HostKeyVerifier>() {
            @Override
            public boolean test(HostKeyVerifier verifier) {
                try {
                    return verifier.verify(hostname, port, key);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                    return false;
                }
            }
        });
    }

    public boolean isEmpty(){
        return this.verifiers.isEmpty();
    }

    public void add(HostKeyVerifier verifier) {
        if (verifier != null) {
            this.verifiers.add(verifier);
        }
    }

    public void clear() {
        this.verifiers.clear();
    }
}
