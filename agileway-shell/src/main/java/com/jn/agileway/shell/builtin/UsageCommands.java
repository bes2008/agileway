package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.CommandGroup;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.langx.util.Strings;

import java.util.List;
@CommandComponent
public class UsageCommands {

    private CommandRegistry commandRegistry;

    public UsageCommands(CommandRegistry commandRegistry){
        this.commandRegistry = commandRegistry;
    }

    @Command("commands")
    public String commandList() {
        StringBuilder builder = new StringBuilder(255);
        List<CommandGroup> commandGroups = commandRegistry.getCommandGroups();
        for (CommandGroup commandGroup: commandGroups){
            builder.append(commandGroup.getName()).append("\t-\t").append(commandGroup.getDesc()).append(Strings.CRLF);
        }
        return builder.toString();
    }
}
