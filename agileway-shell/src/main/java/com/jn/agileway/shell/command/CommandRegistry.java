package com.jn.agileway.shell.command;


import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.comparator.Comparators;
import com.jn.langx.util.comparator.MappingComparator;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * CommandRegistry类负责命令和命令组的注册和检索
 * 它维护了两个映射，一个用于命令组，另一个用于命令
 */
public class CommandRegistry {
    // 保存命令组的映射
    private Map<String, CommandGroup> commandGroupMap = new HashMap<>();
    // 保存命令的映射
    private Map<String, Command> commandMap = new HashMap<String, Command>();

    /**
     * 添加命令组到registry中
     * 如果命令组为null，则不进行任何操作
     *
     * @param commandGroup 要添加的命令组
     */
    public void addCommandGroup(CommandGroup commandGroup) {
        if (commandGroup == null) {
            return;
        }
        this.commandGroupMap.put(commandGroup.getName(), commandGroup);
    }

    /**
     * 根据组名获取命令组列表
     * 如果提供了组名，则返回匹配的命令组列表；否则返回所有命令组
     *
     * @param groupNames 组名数组
     * @return 命令组列表
     */
    public List<CommandGroup> getCommandGroups( String... groupNames){
        return getCommandGroups(false, groupNames);
    }

    /**
     * 根据组名获取命令组列表
     * 如果组名列表为空且getAllIfGroupsIsEmpty为true，则返回所有命令组
     *
     * @param getAllIfGroupsIsEmpty 是否在组名为空时返回所有命令组
     * @param groupNames 组名数组
     * @return 命令组列表
     */
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

    /**
     * 根据组名获取单个命令组
     *
     * @param group 组名
     * @return 对应的命令组，如果不存在则返回null
     */
    public CommandGroup getCommandGroup(String group) {
        return this.commandGroupMap.get(group);
    }

    /**
     * 添加命令到registry中
     * 如果命令为null，则不进行任何操作
     * 如果命令的组不存在，则抛出异常
     *
     * @param command 要添加的命令
     * @throws MalformedCommandException 如果命令的组不存在
     */
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

    /**
     * 根据命令名获取命令
     *
     * @param command 命令名
     * @return 对应的命令，如果不存在则返回null
     */
    public Command getCommand(String command) {
        return commandMap.get(command);
    }

    /**
     * 获取属于特定组的命令列表
     * 如果组不存在，则返回空列表
     * 如果sort为true，命令列表将按命令名排序
     *
     * @param group 组名
     * @param sort 是否对结果进行排序
     * @return 属于该组的命令列表
     */
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
