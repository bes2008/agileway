package com.jn.agileway.text.pinyin;

import com.jn.langx.annotation.Singleton;
import com.jn.langx.registry.Registry;

import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class PinyinEngineRegistry implements Registry<String, PinyinEngine> {

    private ConcurrentHashMap<String, PinyinEngine> cache = new ConcurrentHashMap<String, PinyinEngine>();

    private static final PinyinEngineRegistry registry = new PinyinEngineRegistry();

    private PinyinEngineRegistry() {
    }

    public static PinyinEngineRegistry getInstance() {
        return registry;
    }

    @Override
    public void register(PinyinEngine pinyinEngine) {
        register(pinyinEngine.getName(), pinyinEngine);
    }

    @Override
    public void register(String engineName, PinyinEngine pinyinEngine) {
        cache.putIfAbsent(engineName, pinyinEngine);
    }

    @Override
    public PinyinEngine get(String engineName) {
        return cache.get(engineName);
    }
}
