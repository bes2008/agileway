package com.jn.agileway.zip.archive;

import java.io.InputStream;

public class AutowiredExpanderFactory implements ExpanderFactory{
    @Override
    public Expander get(String format, InputStream inputStream) {
        return null;
    }
}
