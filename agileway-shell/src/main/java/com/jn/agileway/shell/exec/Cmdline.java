package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import org.apache.commons.cli.CommandLine;

public class Cmdline {
    private CommandLine commandLine;
    private Command commandDefinition;

    public Cmdline(Command commandDefinition, CommandLine commandLine) {
        this.commandDefinition = commandDefinition;
        this.commandLine = commandLine;
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public Command getCommandDefinition() {
        return commandDefinition;
    }

    public void setCommandDefinition(Command commandDefinition) {
        this.commandDefinition = commandDefinition;
    }
}
