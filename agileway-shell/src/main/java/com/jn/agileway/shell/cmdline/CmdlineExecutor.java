package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.result.CmdlineExecResult;

public interface CmdlineExecutor {
    CmdExecContext getCmdExecContext();
    void setCmdExecContext(CmdExecContext cmdExecContext);
    void exec(Cmdline cmdline, CmdlineExecResult cmdExecResult);
}
