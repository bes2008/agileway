package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import org.apache.commons.cli.CommandLine;

public class Cmdline {
    private CommandLine parsed;
    private Command command;

    public Cmdline(Command commandDefinition, CommandLine parsed) {
        this.command = commandDefinition;
        this.parsed = parsed;
    }

    public CommandLine getParsed() {
        return parsed;
    }

    public void setParsed(CommandLine parsed) {
        this.parsed = parsed;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }
}
