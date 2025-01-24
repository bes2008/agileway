package com.jn.agileway.shell.cmdline.interactive;

import com.jn.langx.util.function.Supplier0;

public interface BannerSupplier extends Supplier0<String> {
    @Override
    String get();
}
