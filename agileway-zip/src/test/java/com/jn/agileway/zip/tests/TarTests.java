package com.jn.agileway.zip.tests;

import com.jn.agileway.zip.archive.Archiver;
import com.jn.agileway.zip.archive.AutowiredArchiveSuiteFactory;
import com.jn.agileway.zip.archive.Expander;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class TarTests {
    //private String dest_targz = "E:\\tmp\\apache-zookeeper-3.5.6-bin2.tar.gz";
    //private String sour_targz = "E:\\tmp\\apache-zookeeper-3.5.6-bin.tar.gz";
    private String sour_targz = "E:\\tmp\\apache-tomcat-9.0.48.tar.gz";
    private String dest_targz = "E:\\tmp\\apache-tomcat-9.0.48_2.tar.gz";

    @Test
    public void testUncompress() throws Throwable {
        Resource resource = Resources.loadFileResource("file:" + sour_targz);

        Expander expander = AutowiredArchiveSuiteFactory.getInstance().get("tar.gz", resource.getInputStream());
        expander.setOverwriteExistsFiles(true);
        expander.expandTo(new File("e:/tmp/t002"));
        expander.close();
    }

    @Test
    public void testCompress() throws Throwable {
        String targetFile = dest_targz;
        File target = new File(targetFile);
        target.delete();
        target.createNewFile();

        Archiver archiver = AutowiredArchiveSuiteFactory.getInstance().get("tar.gz", new FileOutputStream(target));
        archiver.setIgnoreEmptyDirectory(false);
        archiver.addDirectory(new File("E:\\tmp\\t002\\apache-tomcat-9.0.48"));
        archiver.close();

    }
}
