package com.jn.agileway.zip.archive;

import com.jn.langx.util.Throwables;

import java.io.InputStream;
import java.io.OutputStream;

public class SimpleArchiveSuiteFactory implements SingleArchiveSuiteFactory {
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

    @Override
    public Expander get(String format, InputStream inputStream) {
        Expander expander = null;
        try{
            expander = new Expander(format, inputStream);
        }catch (Throwable ex){
            throw Throwables.wrapAsRuntimeException(ex);
        }
        return expander;
    }

    @Override
    public String getArchiveFormat() {
        return "simple";
    }
}
