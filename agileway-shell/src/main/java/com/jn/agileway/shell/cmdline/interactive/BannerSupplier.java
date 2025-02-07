package com.jn.agileway.shell.cmdline.interactive;

import com.jn.langx.util.function.Supplier0;

/**
 * BannerSupplier 接口继承自 Supplier0 接口，用于获取 String 类型的 banner 信息
 * 该接口的主要作用是提供一个获取 banner 信息的方法，具体的实现类可以根据需要提供不同的 banner 内容
 */
public interface BannerSupplier extends Supplier0<String> {
    /**
     * 获取 banner 信息的方法
     *
     * @return 返回一个 String 类型的 banner 信息
     */
    @Override
    String get();
}
