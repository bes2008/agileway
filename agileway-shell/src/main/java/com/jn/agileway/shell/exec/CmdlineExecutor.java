package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.result.CmdlineExecResult;
import com.jn.langx.environment.Environment;
import com.jn.langx.util.converter.ConverterService;

public interface CmdlineExecutor<C> {

    Environment getEnv();

    void setEnv(Environment env);

    ConverterService getConverterService();

    void setConverterService(ConverterService converterService);

    CommandComponentFactory getComponentFactory();

    void setComponentFactory(CommandComponentFactory componentFactory);

    void setCmdlineParser(CmdlineParser<C> cmdlineParser);

    CmdlineParser<C> getCmdlineParser();

    boolean isExecutable(Command command);
    CmdlineExecResult exec(String[] cmdline, Command command);


}
