package com.jn.agileway.shell.history;

import com.jn.agileway.shell.cmdline.ShellCmdlines;
import com.jn.agileway.shell.command.Command;

public class DefaultHistoryTransformer implements HistoryTransformer{
    @Override
    public String apply(Command command, String[] cmdline) {
        return ShellCmdlines.cmdlineToString(cmdline);
    }
}
