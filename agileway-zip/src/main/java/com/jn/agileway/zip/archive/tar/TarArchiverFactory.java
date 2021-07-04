package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.Archiver;
import com.jn.agileway.zip.archive.ArchiverFactory;
import com.jn.langx.util.Throwables;

import java.io.OutputStream;

public class TarArchiverFactory implements ArchiverFactory {
    @Override
    public Archiver get(String format, OutputStream outputStream) {
        Archiver archiver = null;
        try {
            archiver = new Archiver("tar",
                    outputStream,
                    new TarArchiveOutputStreamCustomizer()
            );
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
        return archiver;
    }
}
