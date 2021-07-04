package com.jn.agileway.zip.archive;

import com.jn.langx.util.function.Supplier2;

import java.io.InputStream;

public interface ExpanderFactory extends Supplier2<String, InputStream, Expander> {
    @Override
    Expander get(String format, InputStream inputStream);
}
