package com.jn.agileway.zip.tests;

import com.jn.agileway.zip.archive.Expander;
import com.jn.agileway.zip.archive.TarFileEntryFileAttrsCopier;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.junit.Test;

import java.io.File;

public class TarPaxHeaderTests {
    @Test
    public void test() throws Throwable {
        Resource resource = Resources.loadClassPathResource("/apache-zookeeper-3.5.6-bin.tar.gz");

        Expander expander = new Expander("tar", new CompressorStreamFactory().createCompressorInputStream("gz", resource.getInputStream()));
        expander.setOverwriteExistsFiles(true);
        expander.setFileAttrCopier(new TarFileEntryFileAttrsCopier());
        expander.expandTo(new File("e:/tmp001"));
        expander.close();
    }
}
