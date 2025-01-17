package com.jn.agileway.shell.command;

import java.util.List;

public class CommandsScanConfig {
    private boolean enabled;
    private List<String> packages;

    public List<String> getPackages() {
        return packages;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }
}
