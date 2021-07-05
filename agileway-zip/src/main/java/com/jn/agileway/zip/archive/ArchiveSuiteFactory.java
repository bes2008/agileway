package com.jn.agileway.zip.archive;


import java.io.InputStream;
import java.io.OutputStream;

public interface ArchiveSuiteFactory {
    Archiver get(String format, OutputStream outputStream);
    Expander get(String format, InputStream inputStream);
}
