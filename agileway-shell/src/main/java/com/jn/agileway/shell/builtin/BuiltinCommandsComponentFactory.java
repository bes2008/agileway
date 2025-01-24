package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.factory.CommandComponentFactory;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;

public class BuiltinCommandsComponentFactory implements CommandComponentFactory {
    private CommandRegistry commandRegistry;
    private CmdlineExecResultHandler cmdExecResultHandler;

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
        return null;
    }
}
