package com.jn.agileway.dmmq.core.translator;

import com.jn.agileway.dmmq.core.MessageTranslator;
import com.jn.langx.util.struct.counter.AtomicIntegerCounter;

import java.util.ArrayList;
import java.util.List;

public class PooledMessageTranslatorFactory implements MessageTranslatorFactory {
    private int capacity;
    private List<MessageTranslator> translatorPool;
    private AtomicIntegerCounter index;

    public PooledMessageTranslatorFactory(int capacity) {
        this.capacity = capacity;
        ArrayList<MessageTranslator> ts = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            ts.add(new DefaultMessageTranslator());
        }
        this.translatorPool = ts;
        this.index = new AtomicIntegerCounter(0);
    }

    private int next() {
        int i = index.getAndIncrement();
        if (index.get() > this.capacity) {
            index.set(0);
        }
        i = i % this.capacity;
        return i;
    }

    @Override
    public MessageTranslator get() {

        int i = next();

        MessageTranslator translator = translatorPool.get(i);
        if (!translator.isIdle()) {

            translator = null;
            while (translator == null) {

                // 扫描一圈
                scan:
                for (int j = 0; j < this.capacity; j++) {
                    i = next();
                    translator = translatorPool.get(i);
                    if (translator.isIdle()) {
                        break scan;
                    } else {
                        translator = null;
                    }
                }
                if (translator == null) {
                    // 稍息一下
                    synchronized (this) {
                        try {
                            Thread.sleep(10);
                        } catch (Throwable ex) {
                            // ignore it
                        }
                    }
                }
            }
        }
        return translator;

    }
}
