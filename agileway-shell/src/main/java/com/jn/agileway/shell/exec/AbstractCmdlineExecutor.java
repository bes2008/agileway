package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.result.CmdlineExecResult;

public abstract class AbstractCmdlineExecutor<C> implements CmdlineExecutor<C>{
    private CmdlineParser<C> parser;
    private CommandComponentFactory factory;
    private CmdExecContext execContext;

    @Override
    public CmdExecContext getCmdExecContext() {
        return execContext;
    }

    @Override
    public void setCmdExecContext(CmdExecContext cmdExecContext) {
        this.execContext=cmdExecContext;
    }

    @Override
    public CommandComponentFactory getCommandComponentFactory() {
        return this.factory;
    }

    @Override
    public void setCommandComponentFactory(CommandComponentFactory factory) {
        this.factory = factory;
    }

    @Override
    public void setCmdlineParser(CmdlineParser<C> cmdlineParser) {
        this.parser = cmdlineParser;
    }

    @Override
    public CmdlineParser<C> getCmdlineParser() {
        return this.parser;
    }

    @Override
    public final CmdlineExecResult exec(String[] cmdline, Command command) {
        CmdlineExecResult execResult = new CmdlineExecResult(cmdline);
        execResult.setCommand(command);
        Cmdline<C> parsedCmdline = null;
        try {
            parsedCmdline = getCmdlineParser().parse(command, cmdline);
        } catch (MalformedCommandException e) {
            execResult.setErr(e);
            return execResult;
        }
        internalExecute(parsedCmdline, execResult);
        return execResult;
    }

    protected abstract void internalExecute(Cmdline<C> cmdline, CmdlineExecResult execResult );

}
