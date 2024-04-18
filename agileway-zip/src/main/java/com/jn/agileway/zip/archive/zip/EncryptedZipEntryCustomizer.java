package com.jn.agileway.zip.archive.zip;

import com.jn.agileway.zip.archive.ArchiveEntryCustomizer;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;


public class EncryptedZipEntryCustomizer implements ArchiveEntryCustomizer {


    @Override
    public void customize(ArchiveEntry entry) {
        ZipArchiveEntry zipEntry = (ZipArchiveEntry) entry;

    }
}
