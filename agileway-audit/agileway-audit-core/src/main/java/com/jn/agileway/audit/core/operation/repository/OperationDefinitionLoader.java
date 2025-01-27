package com.jn.agileway.audit.core.operation.repository;

import com.jn.agileway.audit.core.model.OperationDefinition;
import com.jn.agileway.audit.core.model.OperationImportance;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.configuration.ConfigurationLoader;

import java.util.List;
import java.util.Map;

public interface OperationDefinitionLoader extends ConfigurationLoader<OperationDefinition> {
    @NonNull
    List<OperationDefinition> reload(@Nullable Map<String, OperationImportance> importances);

    void setDefinitionFilePath(@NonNull String definitionFilePath);
}
