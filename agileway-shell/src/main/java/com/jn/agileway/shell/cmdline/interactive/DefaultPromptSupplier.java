package com.jn.agileway.shell.cmdline.interactive;

public class DefaultPromptSupplier implements PromptSupplier {
    private String shellName;

    public DefaultPromptSupplier(String shellName) {
        this.shellName = shellName;
    }

    @Override
    public String get() {
        return shellName;
    }
}
