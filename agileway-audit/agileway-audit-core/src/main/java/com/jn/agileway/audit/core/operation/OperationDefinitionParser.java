package com.jn.agileway.audit.core.operation;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.langx.Nameable;
import com.jn.langx.Parser;

/**
 * OperationDefinition  解析器，解析出OperationDefinition
 *
 * @param <E>
 */
public interface OperationDefinitionParser<E> extends Parser<E, OperationDefinition>, Nameable {
    String getName();

    @Override
    void setName(String s);

    @Override
    OperationDefinition parse(E e);
}
