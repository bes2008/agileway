package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.result.CmdlineExecResult;

public interface CmdlineExecutor<C> {
    CmdExecContext getCmdExecContext();

    void setCmdExecContext(CmdExecContext cmdExecContext);

    CommandComponentFactory getCommandComponentFactory();

    void setCommandComponentFactory(CommandComponentFactory factory);

    CmdlineExecResult exec(String[] cmdline, Command command);

    void setCmdlineParser(CmdlineParser<C> cmdlineParser);
    CmdlineParser<C> getCmdlineParser();
}
