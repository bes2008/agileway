package com.jn.agileway.shell.cmdline.interactive;

import com.jn.langx.util.function.Supplier0;

/**
 * PromptSupplier 接口继承自 Supplier0 接口，用于获取一个字符串类型的提示信息。
 * 该接口主要目的是提供一个标准的方法来获取提示信息，以便在不同的上下文中使用。
 */
public interface PromptSupplier extends Supplier0<String> {
    /**
     * 获取提示信息的方法。
     *
     * @return 返回一个字符串类型的提示信息。
     */
    String get();
}
