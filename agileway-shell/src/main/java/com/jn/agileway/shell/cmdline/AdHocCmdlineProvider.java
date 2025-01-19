package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;

public class AdHocCmdlineProvider implements CmdlineProvider {
    private ApplicationArgs args;
    private boolean appArgsUsed = false;

    public AdHocCmdlineProvider(ApplicationArgs args) {
        this.args = args;
    }

    @Override
    public String[] get() {
        if (appArgsUsed) {
            return null;
        }
        appArgsUsed = true;
        return this.args==null ? null: this.args.getArgs();
    }
}
