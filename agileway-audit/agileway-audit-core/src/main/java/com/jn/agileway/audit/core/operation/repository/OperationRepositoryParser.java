package com.jn.agileway.audit.core.operation.repository;

import com.jn.agileway.audit.core.operation.OperationDefinitionParser;

public interface OperationRepositoryParser extends OperationDefinitionParser<String> {
    String getName();

    String getRepositoryName();
}
