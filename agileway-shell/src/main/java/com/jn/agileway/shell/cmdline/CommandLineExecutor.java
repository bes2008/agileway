package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.result.CmdExecResult;

public interface CommandLineExecutor {
    CmdExecContext getCmdExecContext();
    void setCmdExecContext(CmdExecContext cmdExecContext);
    CmdExecResult exec(Cmdline cmdline);
}
