package com.jn.agileway.shell.cmdline.adhoc;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.cmdline.AbstractCmdlineProvider;

public class AdhocModeCmdlineProvider extends AbstractCmdlineProvider {

    public AdhocModeCmdlineProvider(ApplicationArgs args) {
        this.appArgs = args;
    }

    @Override
    protected String[] nextCmdline() {
        if (appArgsUsed) {
            return null;
        }
        appArgsUsed = true;
        return this.appArgs==null ? null: this.appArgs.getArgs();
    }

    @Override
    protected void handleUnfinishedMultipleLineCommand(String rawLine) {
        System.out.println("unfinished command line: " + rawLine);
    }
}
