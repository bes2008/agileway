package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.cmdline.AnsiFontText;
import com.jn.agileway.shell.command.CommandGroup;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.command.annotation.CommandOption;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;

import java.util.List;

@CommandComponent
public class UsageCommands {

    private CommandRegistry commandRegistry;

    public UsageCommands(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Command(value = "commands", desc = "List commands in some or all groups")
    public String listCommands(
            @CommandOption(value = "a", longName = "all", defaultValue = "false", isFlag = true, desc = "whether list all groups when argument <groups> is empty")
            boolean getAllIfGroupsIsEmpty,
            @CommandArgument(value = "groups", required = false, desc = "the command groups")
            String... groupNames) {
        StringBuilder builder = new StringBuilder(255);
        List<CommandGroup> commandGroups = commandRegistry.getCommandGroups(getAllIfGroupsIsEmpty, groupNames);
        for (CommandGroup commandGroup : commandGroups) {
            String groupName = commandGroup.getName();
            builder.append(new AnsiFontText(Strings.completingLength(groupName, 16, Strings.SP, false)).bold(true))
                    .append("\t\t")
                    .append(commandGroup.getDesc())
                    .append(Strings.CRLF);
            List<com.jn.agileway.shell.command.Command> cmds = this.commandRegistry.getGroupCommands(groupName);
            for (com.jn.agileway.shell.command.Command cmd : cmds) {
                builder.append("\t")
                        .append(new AnsiFontText(Strings.completingLength(cmd.getName(), 12, Strings.SP, false)).bold(true))
                        .append("\t\t")
                        .append(cmd.getDesc())
                        .append(Strings.CRLF);
            }
            builder.append(Strings.CRLF).append(Strings.CRLF);
        }
        return builder.toString();
    }

    @Command(value = "help", desc = "Display the summary")
    public String help(@CommandArgument(value = "commandName", desc = "the command name, if command name has space, quote it") String commandName){
        if(Strings.isEmpty(commandName)){
            return listCommands(true);
        }
        com.jn.agileway.shell.command.Command command = commandRegistry.getCommand(commandName);
        if(command==null){
            throw new RuntimeException(StringTemplates.formatWithPlaceholder("command '' not found", commandName));
        }

        return "";
    }

    private void appHelpInfoTo(StringBuilder builder, com.jn.agileway.shell.command.Command command){
        if(builder==null || command==null){
            return;
        }
        // Usage: <command-name> [Options] [Arguments]
        builder.append("Usage:").append(command.getName());
        if(!Objs.isEmpty(command.getOptionKeys())){
            builder.append(" [<Options>]");
        }
        if(Objs.isNotEmpty(command.getArguments())){
            for (com.jn.agileway.shell.command.CommandArgument argument: command.getArguments()){
                if(argument.isRequired()){
                    builder.append(" <").append(argument.getName()).append(">");
                }else{
                    builder.append(" [<").append(argument.getName()).append(">]");
                }
            }
        }
        builder.append(Strings.CRLF);

        // Options:
        if(!Objs.isEmpty(command.getOptionKeys())){
            builder.append("Options:").append(Strings.CRLF);
            List<String> optionKeys = command.getOptionKeys();

        }

    }

}
