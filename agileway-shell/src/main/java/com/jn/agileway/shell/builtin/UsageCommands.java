package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.cmdline.AnsiFontText;
import com.jn.agileway.shell.command.CommandGroup;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.command.annotation.CommandOption;
import com.jn.langx.util.Strings;

import java.util.List;
@CommandComponent
public class UsageCommands {

    private CommandRegistry commandRegistry;

    public UsageCommands(CommandRegistry commandRegistry){
        this.commandRegistry = commandRegistry;
    }

    @Command(value = "commands", desc = "list commands in some or all groups")
    public String listCommands(
            @CommandOption(value = "g",longName = "groups", required = false)
           String[] groupNames) {
        StringBuilder builder = new StringBuilder(255);
        List<CommandGroup> commandGroups = commandRegistry.getCommandGroups(groupNames);
        for (CommandGroup commandGroup: commandGroups){
            String groupName= commandGroup.getName();
            builder.append(new AnsiFontText(Strings.completingLength(groupName,16, Strings.SP, false)).bold(true)).append("\t-\t").append(commandGroup.getDesc()).append(Strings.CRLF);
            List< com.jn.agileway.shell.command.Command> cmds = this.commandRegistry.getGroupCommands(groupName);
            for (com.jn.agileway.shell.command.Command cmd:cmds){
                builder.append("\t").append(new AnsiFontText(Strings.completingLength(cmd.getName(),12,Strings.SP,false)).bold(true)).append("\t-\t").append( cmd.getDesc()).append(Strings.CRLF);
            }
            builder.append(Strings.CRLF).append(Strings.CRLF);
        }
        return builder.toString();
    }
}
