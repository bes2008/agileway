package com.jn.agileway.shell.suite;

import com.jn.agileway.shell.exec.CmdlineExecutor;
import com.jn.agileway.shell.command.CommandSupplier;

public interface CommandSuite {
    CommandSupplier getCommandSupplier();
    CmdlineExecutor getCmdlineExecutor();
}
