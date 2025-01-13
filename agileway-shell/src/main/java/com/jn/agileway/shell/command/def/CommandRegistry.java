package com.jn.agileway.shell.command.def;

import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.function.Supplier0;

import java.util.HashMap;
import java.util.Map;

public class CommandRegistry {
    private Map<String,CommandGroup> commandGroupMap=new HashMap<String,CommandGroup>();


    public Command getCommand(String command){
        return getCommand("DEFAULT", command);
    }

    public CommandGroup getCommandGroup(String group){
        return commandGroupMap.get(group);
    }

    private CommandGroup getOrCreateCommandGroup(String group){
        return Maps.get(commandGroupMap, group, new Supplier0<CommandGroup>() {
            @Override
            public CommandGroup get() {
                return new CommandGroup();
            }
        });
    }


    public Command getCommand(String group, String command){
        CommandGroup commandGroup = getCommandGroup(group);
        if(commandGroup==null){
            return null;
        }
        return commandGroup.getCommand(command);
    }

    public void addCommand(String group, Command command){
        getOrCreateCommandGroup(group).addCommand(command);
    }

}
