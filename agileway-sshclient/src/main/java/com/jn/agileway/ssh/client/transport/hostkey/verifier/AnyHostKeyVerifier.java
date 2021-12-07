package com.jn.agileway.ssh.client.transport.hostkey.verifier;

import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.List;

public class AnyHostKeyVerifier<PUBKEY> implements HostKeyVerifier<PUBKEY> {
    private final List<HostKeyVerifier<PUBKEY>> verifiers = Collects.emptyArrayList();
    private static final Logger logger = Loggers.getLogger(AnyHostKeyVerifier.class);


    @Override
    public boolean verify(final String hostname, final int port, final String serverHostKeyAlgorithm, final PUBKEY key) {
        return Collects.anyMatch(verifiers, new Predicate<HostKeyVerifier<PUBKEY>>() {
            @Override
            public boolean test(HostKeyVerifier<PUBKEY> verifier) {
                try {
                    return verifier.verify(hostname, port, serverHostKeyAlgorithm, key);
                } catch (Throwable ex) {
                    logger.warn(ex.getMessage(), ex);
                    return false;
                }
            }
        });
    }

    public boolean isEmpty() {
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
