package com.jn.agileway.shell.cmdline;

import com.jn.langx.util.function.Supplier0;

/**
 * 阻塞式使命行提供商，正常结束时，返回null即可。
 */
public interface CmdlineProvider extends Supplier0<String[]> {
    @Override
    String[] get();
}
