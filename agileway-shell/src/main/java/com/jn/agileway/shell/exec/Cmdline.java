package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;

public class Cmdline <C> {
    private C parsed;
    private Command command;

    public Cmdline(Command command, C parsed){
        this.command = command;
        this.parsed = parsed;
    }

    public C getParsed() {
        return parsed;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

}
