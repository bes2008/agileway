package com.jn.agileway.tests.vfs.sftp;

import com.jn.agileway.vfs.provider.AgilewayVFSManagerBootstrap;
import com.jn.agileway.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import com.jn.langx.util.Strings;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.junit.BeforeClass;
import org.junit.Test;

public class AgilewaySftpProviderTests {

    @BeforeClass
    public static void init() throws Throwable {
        AgilewayVFSManagerBootstrap.startup();
    }

    @Test
    public void testListChildren() throws Throwable {
        DefaultFileSystemManager fileSystemManager = (DefaultFileSystemManager) VFS.getManager();

        String url = "sftp://fangjinuo:fjn13570@192.168.1.70:22/vfs_sftp_test";


        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        SftpFileSystemConfigBuilder configBuilder = SftpFileSystemConfigBuilder.getInstance();
        configBuilder.setUserDirIsRoot(fileSystemOptions, true);

        FileObject fileObject = fileSystemManager.resolveFile(url, fileSystemOptions);
        showFile(0, fileObject);

    }

    void showFile(int ident, FileObject fileObject) throws Throwable {
        if (fileObject.isFile()) {
            System.out.println(Strings.repeat("\t", ident) + " |--" + fileObject + " " + fileObject.getContent().getSize() + " " + fileObject.isHidden());
        } else {
            if(fileObject.isFolder()) {
                FileObject[] children = fileObject.getChildren();
                System.out.println(Strings.repeat("\t", ident) + " |--" + fileObject);
                int childIdent = ident + 1;
                for (int i = 0; i < children.length; i++) {
                    showFile(childIdent, children[i]);
                }
            }
        }
    }

}
