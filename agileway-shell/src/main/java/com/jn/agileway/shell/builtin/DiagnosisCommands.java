package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;

@CommandComponent
public class DiagnosisCommands {
    @Command(value = "stacktrace", desc = "Print stacktrace for diagnosis")
    public String stacktrace(
            @CommandArgument(value = "index", desc="")
            int index){
        return null;
    }
}
