package com.jn.agileway.shell.command;

import com.jn.langx.environment.Environment;
import com.jn.langx.util.function.Supplier;

import java.util.List;
import java.util.Map;

/**
 * CommandSupplier 接口继承了 Supplier 接口，用于提供一个根据环境动态生成的命令组集合
 * 它定义了一个特定于环境的命令组获取方法
 */
public interface CommandSupplier extends Supplier<Environment, Map<CommandGroup, List<Command>>> {
    /**
     * 根据给定的环境对象，获取一个映射，该映射将命令组与该组下的命令列表关联起来
     * 这个方法允许根据环境的变化动态地提供不同的命令组和命令
     *
     * @param environment 环境对象，用于确定提供哪些命令组和命令
     * @return 返回一个映射，其中包含根据环境动态确定的命令组和命令
     */
    Map<CommandGroup, List<Command>> get(Environment environment);
}
