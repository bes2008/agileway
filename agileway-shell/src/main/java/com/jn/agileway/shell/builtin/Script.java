package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.Shell;
import com.jn.agileway.shell.cmdline.script.FileCmdlineProvider;
import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;

@CommandComponent
public class Script {
    private Shell shell;

    public Script(){
    }

    public void setShell(Shell shell) {
        this.shell = shell;
    }

    @Command
    public void execScript(
            @CommandArgument(value = "file", desc = "the file path")
            String filepath){
        this.shell.run(new FileCmdlineProvider(null, filepath));
    }
}
