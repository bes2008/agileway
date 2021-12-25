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
        doUncompress(sour_targz, "e:/tmp/t002");
    }
    private void doUncompress(String sour_targz, String destDir) throws Throwable {
        doUncompress(null, sour_targz, destDir);
    }
    private void doUncompress(String format, String sour_targz, String destDir) throws Throwable {
        if(format ==null){
            format = "tar.gz";
        }
        Resource resource = Resources.loadFileResource("file:" + sour_targz);
        Expander expander = AutowiredArchiveSuiteFactory.getInstance().get(format, resource.getInputStream());
        expander.setOverwriteExistsFiles(true);
        expander.expandTo(new File(destDir));
        expander.close();
    }

    @Test
    public void testCompress() throws Throwable {
        doCompress(dest_targz);

    }

    private void doCompress(String targetFile) throws Throwable {
        File target = new File(targetFile);
        target.delete();
        target.createNewFile();

        Archiver archiver = AutowiredArchiveSuiteFactory.getInstance().get("tar.gz", new FileOutputStream(target));
        archiver.setIgnoreEmptyDirectory(false);
        archiver.addDirectory(new File("E:\\tmp\\t002\\apache-tomcat-9.0.48"));
        archiver.close();
    }


    @Test
    public void testUncompress2() throws Throwable {
        doUncompress("whl","D:\\pip_repo\\setuptools\\40.4.3\\setuptools-40.4.3-py2.py3-none-any.whl", "D:\\pip_repo\\setuptools\\40.4.3\\setuptools-40_4_3-py2_py3-none-any_whl");
        Thread.sleep(3000);
    }

    @Test
    public void testUncompress3() throws Throwable {
        doUncompress("whl","D:\\pip_repo\\django\\1.8.19\\django-1.8.19-py2.py3-none-any.whl", "D:\\pip_repo\\django\\1.8.19\\django-1_8_19-py2_py3-none-any_whl");
        Thread.sleep(3000);
    }
}
