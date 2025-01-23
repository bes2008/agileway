package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.cmdline.AnsiText;
import com.jn.agileway.shell.command.CommandGroup;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandArgument;
import com.jn.agileway.shell.command.annotation.CommandComponent;
import com.jn.agileway.shell.command.annotation.CommandOption;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import org.apache.commons.cli.Option;

import java.util.List;

@CommandComponent
public class UsageCommands {

    private CommandRegistry commandRegistry;

    public UsageCommands(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Command(value = "commands", desc = "List commands in some or all groups")
    public String listCommands(
            @CommandOption(value = "a", longName = "all",  isFlag = true, desc = "whether list all groups when argument <groups> is empty")
            boolean getAllIfGroupsIsEmpty,
            @CommandOption(value = "s", longName = "sort", isFlag = true, desc = "sort the commands")
            boolean sort,
            @CommandArgument(value = "groups", desc = "the command groups")
            String... groupNames) {
        StringBuilder builder = new StringBuilder(255);
        List<CommandGroup> commandGroups = commandRegistry.getCommandGroups(getAllIfGroupsIsEmpty, groupNames);
        for (CommandGroup commandGroup : commandGroups) {
            String groupName = commandGroup.getName();
            builder.append(new AnsiText(Strings.completingLength(groupName, 16, Strings.SP, false)).bold(true))
                    .append("\t\t")
                    .append(commandGroup.getDesc())
                    .append(Strings.CRLF);
            List<com.jn.agileway.shell.command.Command> cmds = this.commandRegistry.getGroupCommands(groupName, sort);
            for (com.jn.agileway.shell.command.Command cmd : cmds) {
                builder.append("\t")
                        .append(new AnsiText(Strings.completingLength(cmd.getName(), 12, Strings.SP, false)).bold(true))
                        .append("\t\t")
                        .append(cmd.getDesc())
                        .append(Strings.CRLF);
            }
            builder.append(Strings.CRLF).append(Strings.CRLF);
        }
        return builder.toString();
    }

    @Command(value = "help", desc = "Display the summary")
    public String help(
            @CommandArgument(value = "commandName", desc = "the command name, if command name has space, quote it", defaultValue = "")
            String commandName){
        if(Strings.isEmpty(commandName)){
            return listCommands(true,true);
        }
        com.jn.agileway.shell.command.Command command = commandRegistry.getCommand(commandName);
        if(command==null){
            throw new RuntimeException(StringTemplates.formatWithPlaceholder("command '{}' not found", commandName));
        }
        StringBuilder builder = new StringBuilder(255);
        appHelpInfoTo(builder, command);
        return builder.toString();
    }

    private void appHelpInfoTo(StringBuilder builder, com.jn.agileway.shell.command.Command command){
        if(builder==null || command==null){
            return;
        }
        // Usage: <command-name> [Options] [Arguments]
        builder.append("Usage:").append(Strings.CRLF).append("\t").append(AnsiText.ofBoldText( command.getName() ));
        if(!Objs.isEmpty(command.getOptionKeys())){
            builder.append(" [<Options>]");
        }
        if(Objs.isNotEmpty(command.getArguments())){
            for (com.jn.agileway.shell.command.CommandArgument argument: command.getArguments()){
                builder.append(" ");
                if(!argument.isRequired()){
                    builder.append("[");
                }
                builder.append("<").append(argument.getName()).append(">");
                if(argument.isMultipleValue()){
                    builder.append("...");
                }
                if(!argument.isRequired()){
                    builder.append("]");
                }
            }
        }
        builder.append(Strings.CRLF);
        builder.append(Strings.CRLF);

        // Desc:
        builder.append(command.getDesc()).append(Strings.CRLF);
        builder.append(Strings.CRLF);

        // Options:
        if(!Objs.isEmpty(command.getOptionKeys())){

            builder.append("Options:").append(Strings.CRLF);
            List<String> optionKeys = command.getOptionKeys();
            for(String optionKey : optionKeys){
                Option option = command.getOptions().getOption(optionKey);
                builder.append("\t");
                String shortName = option.getOpt();
                int outOptionNameCount=0;

                if(Objs.isNotEmpty(shortName)){
                    outOptionNameCount++;
                    builder.append(AnsiText.ofBoldText("-"+shortName));
                    if(option.hasArgName()){
                        builder.append(" ");
                        if(option.hasOptionalArg()){
                            builder.append("[").append(option.getArgName()).append("]");
                        }else{
                            builder.append(option.getArgName());
                        }
                    }
                }
                String longName = option.getLongOpt();
                if(Objs.isNotEmpty(longName)){
                    if(outOptionNameCount>0){
                        builder.append(", ");
                    }
                    builder.append(AnsiText.ofBoldText("--"+longName));

                    if(option.hasArgName()){
                        builder.append(" ");
                        if(option.hasOptionalArg()){
                            builder.append("[").append(option.getArgName()).append("]");
                        }else{
                            builder.append(option.getArgName());
                        }
                    }
                }

                builder.append("\t\t");
                builder.append(option.getDescription());
                builder.append(Strings.CRLF);
            }
            builder.append(Strings.CRLF);
        }

        // Arguments
        if(Objs.isNotEmpty(command.getArguments())){
            builder.append("Args:").append(Strings.CRLF);

            List<com.jn.agileway.shell.command.CommandArgument> arguments = command.getArguments();
            for(com.jn.agileway.shell.command.CommandArgument argument:arguments){
                builder.append("\t");
                builder.append(AnsiText.ofBoldText(argument.getName()));
                builder.append("\t\t");
                builder.append(argument.getDesc());
                builder.append("; ");
                builder.append(AnsiText.ofBoldText(argument.isRequired()? "Required": "Optional"));
                builder.append("; ");
                if(!argument.isRequired()){
                    builder.append("defaultValue: ").append(argument.isMultipleValue()?Strings.join(" ", argument.getDefaultValues()): argument.getDefaultValue());
                }
                builder.append(Strings.CRLF);
            }
            builder.append(Strings.CRLF);
        }
    }

}
