package com.jn.agileway.shell.history;

import com.jn.agileway.shell.command.Command;
import com.jn.langx.util.function.Function2;
/**
 * @since 5.1.1 用于在记录日志之前，对 cmdline 进行一些处理，比如：
 * 1. 去掉敏感信息
 * 2. 去掉一些不需要记录的 cmdline
 *
 * 如果处理后是 null，empty，则不记录该 cmdline
 * @see DefaultHistoryTransformer
 */
public interface HistoryTransformer extends Function2<Command, String[], String> {
    @Override
    String apply(Command command, String[] cmdline);
}
