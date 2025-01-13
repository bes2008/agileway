package com.jn.agileway.shell.command.def;

import com.jn.langx.util.collection.Collects;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandGroup {
    private String name;
    private final Map<String, Command> definitionMap = new LinkedHashMap<>();

    public void addCommand(Command command){
        this.definitionMap.put(command.getName(),command);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Command> getCommands(){
        return Collects.asList(definitionMap.values());
    }

    public Command getCommand(String commandName){
        return definitionMap.get(commandName);
    }

}
