package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.result.CmdlineExecResult;

public interface CmdlineExecutor {
    CmdExecContext getCmdExecContext();
    void setCmdExecContext(CmdExecContext cmdExecContext);
    CmdlineExecResult exec(String[] cmdline, Command command);
}
