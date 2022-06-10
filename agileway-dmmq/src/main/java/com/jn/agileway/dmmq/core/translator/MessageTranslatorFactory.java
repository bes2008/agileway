package com.jn.agileway.dmmq.core.translator;

import com.jn.agileway.dmmq.core.MessageTranslator;
import com.jn.langx.util.function.Supplier0;

public interface MessageTranslatorFactory<T> extends Supplier0<MessageTranslator> {
    @Override
    MessageTranslator get();
}
