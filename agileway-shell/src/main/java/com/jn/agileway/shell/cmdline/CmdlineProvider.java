package com.jn.agileway.shell.cmdline;

import com.jn.langx.util.function.Supplier0;

/**
 * 阻塞式使命行提供商，正常结束时，返回null即可。
 */
public interface CmdlineProvider extends Supplier0<String[]> {
    /**
     * 获取一个命令
     *
     * <pre>
     * 1. 返回 Null 代表结束 shell
     * 2. 返回 空数组 代表输入了空行，没有命令
     * 3. 非空，则按照命令去处理
     * </pre>
     *
     * 输入过程出现 异常，需要转换为 ShellInterruptedException 异常
     */
    @Override
    String[] get();
}
