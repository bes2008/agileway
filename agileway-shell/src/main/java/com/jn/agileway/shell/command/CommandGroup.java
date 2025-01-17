package com.jn.agileway.shell.command;

import com.jn.langx.util.collection.Collects;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandGroup {
    public static final String DEFAULT_GROUP="DEFAULT";
    private String name;
    private String desc;
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

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }



}
