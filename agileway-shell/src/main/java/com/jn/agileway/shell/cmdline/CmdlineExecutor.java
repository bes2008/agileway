package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.result.CmdExecResult;

public interface CmdlineExecutor {
    CmdExecContext getCmdExecContext();
    void setCmdExecContext(CmdExecContext cmdExecContext);
    CmdExecResult exec(Cmdline cmdline);
}
