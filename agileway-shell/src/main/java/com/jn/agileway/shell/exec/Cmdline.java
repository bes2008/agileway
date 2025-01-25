package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import org.apache.commons.cli.CommandLine;

public class Cmdline {
    private CommandLine parsed;
    private Command commandDefinition;

    public Cmdline(Command commandDefinition, CommandLine parsed) {
        this.commandDefinition = commandDefinition;
        this.parsed = parsed;
    }

    public CommandLine getParsed() {
        return parsed;
    }

    public void setParsed(CommandLine parsed) {
        this.parsed = parsed;
    }

    public Command getCommandDefinition() {
        return commandDefinition;
    }

    public void setCommandDefinition(Command commandDefinition) {
        this.commandDefinition = commandDefinition;
    }
}
