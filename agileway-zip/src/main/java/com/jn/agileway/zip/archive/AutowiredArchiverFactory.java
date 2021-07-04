package com.jn.agileway.zip.archive;

import com.jn.agileway.zip.format.ZipFormat;
import com.jn.agileway.zip.format.ZipFormats;
import com.jn.langx.util.Preconditions;

import java.io.OutputStream;

public class AutowiredArchiverFactory implements ArchiverFactory{
    @Override
    public Archiver get(String format, OutputStream outputStream) {
        ZipFormat zipFormat = ZipFormats.getZipFormat(format);
        Preconditions.checkNotNull(zipFormat);

        return null;
    }
}
