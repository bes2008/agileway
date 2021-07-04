package com.jn.agileway.zip.archive;

import com.jn.langx.util.function.Supplier2;

import java.io.OutputStream;

public interface ArchiverFactory extends Supplier2<String, OutputStream, Archiver> {
    @Override
    Archiver get(String format, OutputStream outputStream);
}
