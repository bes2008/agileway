package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.exception.MalformedCommandLineException;

/**
 * 定义一个命令行解析器接口，用于将原始命令行字符串数组解析为结构化的命令行对象
 * 这个接口的主要作用是提供一个标准方法，以便外部可以通过命令对象和原始命令行参数来操作命令行数据
 *
 * @param <C> 泛型参数，表示命令行数据中可能包含的特定类型
 */
public interface CmdlineParser<C> {
    /**
     * 解析命令行参数的方法
     *
     * @param command 命令对象，包含了命令行的基本信息和结构
     * @param raw 原始命令行参数数组，通常是从程序的 main 方法传入
     * @return 解析后的结构化命令行对象
     * @throws MalformedCommandLineException 当命令行参数格式错误或无法解析时抛出此异常
     */
    Cmdline<C> parse(Command command, String[] raw) throws MalformedCommandLineException;
}
