package com.jn.agileway.zip.archive;

import com.jn.langx.util.Throwables;

import java.io.OutputStream;

public class SimpleArchiverFactory implements ArchiverFactory {
    @Override
    public Archiver get(String format, OutputStream outputStream) {
        Archiver archiver = null;
        try {
            archiver = new Archiver(format, outputStream, null);
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
        return archiver;
    }
}
