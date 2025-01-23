package com.jn.agileway.shell.cmdline.interactive;

import com.jn.langx.util.function.Supplier0;

public interface PromptSupplier extends Supplier0<String> {
    String get();
}
