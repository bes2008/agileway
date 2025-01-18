package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.result.CmdExecResult;

public interface CommandLineExecutor {
    CmdExecContext getShellContext();
    CmdExecResult exec(Cmdline cmdline);
}
