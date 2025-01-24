package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.factory.CommandComponentFactory;
import com.jn.agileway.shell.history.HistoryHandler;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;

public class BuiltinCommandsComponentFactory implements CommandComponentFactory {
    private CommandRegistry commandRegistry;
    private CmdlineExecResultHandler cmdExecResultHandler;
    private HistoryHandler historyHandler;

    public void setHistoryHandler(HistoryHandler historyHandler) {
        this.historyHandler = historyHandler;
    }

    public void setCommandRegistry(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    public void setCmdExecResultHandler(CmdlineExecResultHandler cmdExecResultHandler) {
        this.cmdExecResultHandler = cmdExecResultHandler;
    }

    @Override
    public Object get(Class type) {
        if(type == Usage.class){
            return new Usage(this.commandRegistry);
        }
        if(type == Diagnosis.class){
            return new Diagnosis(this.cmdExecResultHandler);
        }
        if(type == History.class){
            return new History(this.historyHandler);
        }
        return null;
    }
}
