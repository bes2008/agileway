package com.jn.agileway.zip.archive.tar;

import com.jn.agileway.zip.archive.ArchiveOutputStreamCustomizer;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class TarArchiveOutputStreamCustomizer implements ArchiveOutputStreamCustomizer {
    @Override
    public void customize(ArchiveOutputStream archiveOutputStream) {
        TarArchiveOutputStream tarOut = (TarArchiveOutputStream)archiveOutputStream;
        tarOut.setAddPaxHeadersForNonAsciiNames(true);
        tarOut.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        tarOut.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
    }
}
