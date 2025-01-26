package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.exec.CommandComponentFactory;
import com.jn.agileway.shell.history.HistoryHandler;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;

public class BuiltinCommandsComponentFactory implements CommandComponentFactory {
    private Shell shell;

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Override
    public Object get(Class type) {
        if(type == Usage.class){
            return new Usage(this.shell.getCommandRegistry());
        }
        if(type == Diagnosis.class){
            return new Diagnosis(this.shell.getExecResultHandler());
        }
        if(type == History.class){
            return new History(this.shell.getHistoryHandler());
        }
        if(type == Script.class){
            Script script=  new Script();
            script.setShell(shell);
            return script;
        }
        return null;
    }
}
