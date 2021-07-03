package com.jn.agileway.zip.tests;

import com.jn.agileway.zip.archive.Archiver;
import com.jn.agileway.zip.archive.Expander;
import com.jn.agileway.zip.archive.tar.TarArchiveOutputStreamCustomizer;
import com.jn.agileway.zip.archive.tar.TarFileEntryFileAttrsCopier;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class TarTests {
    @Test
    public void testUncompress() throws Throwable {
        Resource resource = Resources.loadFileResource("file:E:\\tmp001\\apache-zookeeper-3.5.6-bin2.tar.gz");

        Expander expander = new Expander("tar", new CompressorStreamFactory().createCompressorInputStream("gz", resource.getInputStream()));
        expander.setOverwriteExistsFiles(true);
        expander.setFileAttrCopier(new TarFileEntryFileAttrsCopier());
        expander.expandTo(new File("e:/tmp001/t002"));
        expander.close();
    }

    @Test
    public void testCompress() throws Throwable {
        String targetFile = "E:\\tmp001\\apache-zookeeper-3.5.6-bin2.tar.gz";
        File target = new File(targetFile);
        target.delete();
        target.createNewFile();

        Archiver archiver = new Archiver("tar",
                new CompressorStreamFactory().createCompressorOutputStream("gz", new FileOutputStream(target)),
                new TarArchiveOutputStreamCustomizer()
                );
        archiver.addDirectory(new File("E:\\tmp001\\apache-zookeeper-3.5.6-bin"));
        archiver.close();

    }
}
