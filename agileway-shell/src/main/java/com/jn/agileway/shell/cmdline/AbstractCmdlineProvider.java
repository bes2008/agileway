package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.langx.lifecycle.AbstractInitializable;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;

import java.io.IOException;

/**
 * @since 5.1.1
 */
public abstract class AbstractCmdlineProvider extends AbstractInitializable implements CmdlineProvider{

    protected ApplicationArgs appArgs;
    protected boolean appArgsUsed = false;

    @Override
    public final String[] get() {
        String[] cmdline = nextCmdline();
        if(cmdline == null || cmdline.length==0){
            return cmdline;
        }

        String lastPart = cmdline[cmdline.length-1];
        if (Strings.endsWith(lastPart, "\\")) {
            handleUnfinishedMultipleLineCommand(ShellCmdlines.cmdlineToString(cmdline));
            return Emptys.EMPTY_STRINGS;
        }

        return cmdline;
    }

    protected abstract void handleUnfinishedMultipleLineCommand(String rawLine);

    protected abstract String[] nextCmdline();

    @Override
    public void close() throws IOException {
    }
}
