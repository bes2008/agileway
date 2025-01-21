package com.jn.agileway.shell.cmdline.adhoc;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.cmdline.CmdlineProvider;

public class AdhocModeCmdlineProvider implements CmdlineProvider {
    private ApplicationArgs args;
    private boolean appArgsUsed = false;

    public AdhocModeCmdlineProvider(ApplicationArgs args) {
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
