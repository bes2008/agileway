package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.exception.MalformedCommandLineException;
import com.jn.agileway.shell.result.CmdlineExecResult;
import com.jn.langx.environment.Environment;
import com.jn.langx.util.converter.ConverterService;

public abstract class CmdlineExecutor<C>{
    private CmdlineParser<C> cmdlineParser;
    private Environment env;
    private ConverterService converterService;
    private CommandComponentFactory componentFactory;


    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public ConverterService getConverterService() {
        return converterService;
    }

    public void setConverterService(ConverterService converterService) {
        this.converterService = converterService;
    }

    public CommandComponentFactory getComponentFactory() {
        return componentFactory;
    }

    public void setComponentFactory(CommandComponentFactory componentFactory) {
        this.componentFactory = componentFactory;
    }

    public void setCmdlineParser(CmdlineParser<C> cmdlineParser) {
        this.cmdlineParser = cmdlineParser;
    }

    public CmdlineParser<C> getCmdlineParser() {
        return this.cmdlineParser;
    }
    public abstract boolean isExecutable(Command command);
    public final CmdlineExecResult exec(String[] cmdline, Command command) {
        CmdlineExecResult execResult = new CmdlineExecResult(cmdline);
        execResult.setCommand(command);
        Cmdline<C> parsedCmdline = null;
        try {
            parsedCmdline = getCmdlineParser().parse(command, cmdline);
        } catch (MalformedCommandLineException e) {
            execResult.setErr(e);
            return execResult;
        }
        try {
            Object methodResult = internalExecute(parsedCmdline);
            execResult.setStdoutData(methodResult);
        }catch (Throwable e){
            execResult.setErr(e);
        }

        return execResult;
    }

    protected abstract Object internalExecute(Cmdline<C> cmdline);

}
