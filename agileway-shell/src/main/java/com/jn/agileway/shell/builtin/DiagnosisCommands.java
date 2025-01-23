package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.result.CmdlineExecResultHandler;

@CommandComponent
public class DiagnosisCommands {
    private CmdlineExecResultHandler cmdlineExecResultHandler;
    public DiagnosisCommands(CmdlineExecResultHandler cmdlineExecResultHandler){
        this.cmdlineExecResultHandler = cmdlineExecResultHandler;
    }

    @Command(value = "stacktrace", desc = "Print stacktrace for diagnosis")
    public String stacktrace(
            @CommandArgument(value = "index", defaultValue = "9", desc="Print <N>th stacktrace, valid N value is [-10, 9] ")
            int index){
        return cmdlineExecResultHandler.getStacktrace(index);
    }
}
