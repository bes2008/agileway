package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.Archiver;
import com.jn.agileway.zip.archive.Expander;
import com.jn.agileway.zip.archive.SingleArchiveSuiteFactory;
import com.jn.langx.util.Throwables;

import java.io.InputStream;
import java.io.OutputStream;

public class TarArchiveSuiteFactory implements SingleArchiveSuiteFactory {
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

    @Override
    public Expander get(String format, InputStream inputStream) {
        Expander expander = null;
        try{
            expander = new Expander(format, inputStream);
            expander.setFileAttrCopier(new TarFileEntryFileAttrsCopier());
        }catch (Throwable ex){
            throw Throwables.wrapAsRuntimeException(ex);
        }
        return expander;
    }

    @Override
    public String getArchiveFormat() {
        return "tar";
    }
}
