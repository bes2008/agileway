package com.jn.agileway.ssh.client.transport.hostkey.keytype;

import com.jn.agileway.ssh.client.transport.hostkey.codec.PublicKeyCodecRegistry;
import com.jn.langx.annotation.Singleton;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.lifecycle.InitializationException;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.struct.Holder;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class DefaultPublicKeyHostKeyTypeExtractor extends AbstractInitializable implements PublicKeyHostKeyTypeExtractor {
    private static final DefaultPublicKeyHostKeyTypeExtractor INSTANCE = new DefaultPublicKeyHostKeyTypeExtractor();
    private Map<String, PublicKeyHostKeyTypeExtractor> extractorMap = new HashMap<String, PublicKeyHostKeyTypeExtractor>();

    private DefaultPublicKeyHostKeyTypeExtractor() {
        init();
    }

    /**
     * @since 2.7.11
     */
    @Override
    protected void doInit(){
        PublicKeyCodecRegistry.getInstance();
    }

    public static DefaultPublicKeyHostKeyTypeExtractor getInstance() {
        return INSTANCE;
    }

    public void addPublicKeyHostKeyTypeExtractor(String name, PublicKeyHostKeyTypeExtractor extractor) {
        extractorMap.put(name, extractor);
    }

    @Override
    public String get(final PublicKey publicKey) {
        final Holder<String> keyType = new Holder<String>();
        Collects.forEach(this.extractorMap.values(), new Consumer<PublicKeyHostKeyTypeExtractor>() {
            @Override
            public void accept(PublicKeyHostKeyTypeExtractor extractor) {
                String t = extractor.get(publicKey);
                keyType.set(t);
            }
        }, new Predicate<PublicKeyHostKeyTypeExtractor>() {
            @Override
            public boolean test(PublicKeyHostKeyTypeExtractor extractor) {
                return !keyType.isEmpty();
            }
        });
        return keyType.get();
    }
}
