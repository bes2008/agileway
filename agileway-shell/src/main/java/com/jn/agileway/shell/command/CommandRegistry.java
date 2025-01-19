package com.jn.agileway.shell.command;


import com.jn.langx.util.Objs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
    private Map<String, CommandGroup> commandGroupMap = new HashMap<>();
    private Map<String,Command> commandMap=new HashMap<String,Command>();

    public Command getCommand(String command){
        return commandMap.get(command);
    }

    public CommandGroup getCommandGroup(String group){
        return this.commandGroupMap.get(group);
    }

    public void addCommand(Command command){
        if(command==null){
            return;
        }
        this.commandMap.put(command.getName(), command);
        if(Objs.isNotEmpty(command.getAlias())){
            List<Command> aliasCommands = command.newCommandsForAlias();
            for (Command aliasCommand : aliasCommands) {
                this.commandMap.put(aliasCommand.getName(), aliasCommand);
            }
        }
    }

    public void addCommandGroup(CommandGroup commandGroup){
        if(commandGroup==null){
            return;
        }
        this.commandGroupMap.put(commandGroup.getName(), commandGroup);
    }
}
