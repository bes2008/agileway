package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.exception.MalformedCommandLineException;

public interface CmdlineParser<C> {
    Cmdline<C> parse(Command command, String[] raw) throws MalformedCommandLineException;
}
