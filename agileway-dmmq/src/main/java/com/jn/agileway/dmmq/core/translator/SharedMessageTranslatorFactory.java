package com.jn.agileway.dmmq.core.translator;

import com.jn.agileway.dmmq.core.MessageTranslator;

public class SharedMessageTranslatorFactory implements MessageTranslatorFactory {
    private MessageTranslator translator = new DefaultMessageTranslator();

    public SharedMessageTranslatorFactory() {

    }

    public SharedMessageTranslatorFactory(MessageTranslator translator) {
        this.setTranslator(translator);
    }

    public void setTranslator(MessageTranslator translator) {
        if (translator != null) {
            this.translator = translator;
        }
    }

    @Override
    public MessageTranslator get() {
        if (!this.translator.isIdle()) {
            synchronized (this.translator) {
                while (!this.translator.isIdle()) {
                    try {
                        translator.wait(10);
                    } catch (InterruptedException ex) {
                        // ignore it
                    }
                }
            }
        }
        return this.translator;
    }
}
