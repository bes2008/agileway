package com.jn.agileway.dmmq.core.translator;

import com.jn.agileway.dmmq.core.MessageTranslator;

public class DefaultMessageTranslatorFactory implements MessageTranslatorFactory {
    @Override
    public MessageTranslator get() {
        return new DefaultMessageTranslator();
    }
}
