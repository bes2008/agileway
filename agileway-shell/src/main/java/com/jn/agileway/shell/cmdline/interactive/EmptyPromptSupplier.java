package com.jn.agileway.shell.cmdline.interactive;

public class EmptyPromptSupplier implements PromptSupplier{
    @Override
    public String get() {
        return "";
    }
}
