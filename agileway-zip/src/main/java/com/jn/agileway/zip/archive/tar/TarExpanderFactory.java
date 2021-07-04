package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.Expander;
import com.jn.agileway.zip.archive.ExpanderFactory;
import com.jn.langx.util.Throwables;

import java.io.InputStream;

public class TarExpanderFactory implements ExpanderFactory {
    @Override
    public Expander get(String format, InputStream inputStream) {
        Expander expander = null;
        try {
            expander = new Expander("tar", inputStream);
            expander.setFileAttrCopier(new TarFileEntryFileAttrsCopier());
            return expander;
        } catch (Throwable ex) {
            throw Throwables.wrapAsRuntimeException(ex);
        }
    }
}
