package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.exception.ShellInterruptedException;

@CommandComponent
public class Quit {
    @Command(value = "quit", alias = {"exit"})
    public void quit(){
        throw new ShellInterruptedException("quit", 0);
    }
}
