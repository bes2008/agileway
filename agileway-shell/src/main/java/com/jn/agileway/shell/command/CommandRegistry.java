package com.jn.agileway.shell.command;


import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.comparator.Comparators;
import com.jn.langx.util.comparator.DelegatableComparator;
import com.jn.langx.util.comparator.MappingComparator;
import com.jn.langx.util.comparator.OrderedComparator;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.util.*;

public class CommandRegistry {
    private Map<String, CommandGroup> commandGroupMap = new HashMap<>();
    private Map<String, Command> commandMap = new HashMap<String, Command>();

    public void addCommandGroup(CommandGroup commandGroup) {
        if (commandGroup == null) {
            return;
        }
        this.commandGroupMap.put(commandGroup.getName(), commandGroup);
    }

    public List<CommandGroup> getCommandGroups( String... groupNames){
        return getCommandGroups(false, groupNames);
    }

    public List<CommandGroup> getCommandGroups(boolean getAllIfGroupsIsEmpty, String... groupNames) {
        List<CommandGroup> groups = new ArrayList<>();

        if(Objs.isNotEmpty(groupNames)) {
            for (String groupName : groupNames) {
                CommandGroup group = this.commandGroupMap.get(groupName);
                if (group != null) {
                    groups.add(group);
                }
            }
        }
        if(groups.isEmpty() && getAllIfGroupsIsEmpty){
            groups= Lists.newArrayList(this.commandGroupMap.values());
        }
        return groups;

    }

    public CommandGroup getCommandGroup(String group) {
        return this.commandGroupMap.get(group);
    }

    public void addCommand(Command command) {
        if (command == null) {
            return;
        }
        String groupName = command.getGroup();
        if (groupName == null || !this.commandGroupMap.containsKey(groupName)) {
            throw new MalformedCommandException("group is not found, please use @CommandGroup");
        }

        this.commandMap.put(command.getName(), command);

        if (command.getOutputTransformer() == null) {
            CommandGroup commandGroup = getCommandGroup(groupName);
            command.setOutputTransformer(commandGroup.getOutputTransformer());
        }
        if (Objs.isNotEmpty(command.getAlias())) {
            List<Command> aliasCommands = command.newCommandsForAlias();
            for (Command aliasCommand : aliasCommands) {
                this.commandMap.put(aliasCommand.getName(), aliasCommand);
            }
        }
    }

    public Command getCommand(String command) {
        return commandMap.get(command);
    }

    public List<Command> getGroupCommands(String group, boolean sort) {
        if (!this.commandGroupMap.containsKey(group)) {
            return Lists.immutableList();
        }
        Pipeline<Command> pipeline = Pipeline.of(this.commandMap.values())
                .filter(new Predicate<Command>() {
                    @Override
                    public boolean test(Command command) {
                        return Objs.equals(command.getGroup(), group);
                    }
                });
        if(sort){
            pipeline = pipeline.sorted(new MappingComparator<Command, String>(Comparators.STRING_COMPARATOR_IGNORE_CASE, new Function<Command, String>() {
                @Override
                public String apply(Command command) {
                    return command.getName();
                }
            }));
        }
        return pipeline.asList();
    }

}
