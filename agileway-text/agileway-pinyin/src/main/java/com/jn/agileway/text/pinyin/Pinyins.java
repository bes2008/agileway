package com.jn.agileway.text.pinyin;

import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

public class Pinyins {
    private static final Logger logger = LoggerFactory.getLogger(Pinyins.class);

    private static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";

    private static final PinyinEngineRegistry registry = PinyinEngineRegistry.getInstance();

    static {
        ServiceLoader<PinyinEngine> loader = ServiceLoader.load(PinyinEngine.class);
        Pipeline.of(loader).forEach(new Consumer<PinyinEngine>() {
            @Override
            public void accept(PinyinEngine engine) {
                registry.register(engine);
                logger.info("Load the pinyin engine {}", engine.getName());
            }
        });
    }

    /**
     * 获得全局单例的拼音引擎
     *
     * @return 全局单例的拼音引擎
     */
    public static PinyinEngine getEngine(String engineName) {
        return registry.get(engineName);
    }

    public static PinyinEngine getEngine() {
        return registry.get("langx_pinyin");
    }

    /**
     * 是否为中文字符
     *
     * @param c 字符
     * @return 是否为中文字符
     */
    public static boolean isChinese(char c) {
        return '〇' == c || String.valueOf(c).matches(CHINESE_REGEX);
    }
}