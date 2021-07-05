package com.jn.agileway.zip.tests;

import com.jn.agileway.zip.archive.AutowiredArchiveSuiteFactory;
import com.jn.agileway.zip.archive.Expander;
import org.junit.Test;

import java.io.File;

public class ZipTests {

    @Test
    public void testUncompress() throws Throwable{
        String filepath = "e:/tmp/camunda-demo-master.zip";

        Expander expander = AutowiredArchiveSuiteFactory.getInstance().createExpander(filepath);
        expander.expandTo(new File("e:/tmp/"));
    }

}
