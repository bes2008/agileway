package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.result.CmdExecResult;

public interface CommandLineExecutor {
    CmdExecContext getCmdExecContext();
    CmdExecResult exec(Cmdline cmdline);
}
