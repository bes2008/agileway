package com.jn.agileway.audit.core.model;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.collection.CommonProps;

import java.util.Map;

/**
 * 代表了AuditEvent中的Operation
 */
public class Operation extends CommonProps {
    /**
     * 操作定义
     */
    private OperationDefinition definition;
    /**
     * 执行Operation时，提供的参数
     */
    private Map<String, ?> parameters; // {optional}
    /**
     * 操作成功或失败
     */
    private OperationResult result;

    public OperationDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(OperationDefinition definition) {
        this.definition = definition;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, ?> parameters) {
        this.parameters = parameters;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public static void copyTo(@NonNull Operation source, @NonNull Operation destination) {
        destination.setResult(source.getResult());
        destination.setParameters(source.getParameters());
        destination.setDefinition(source.getDefinition());
    }
}
