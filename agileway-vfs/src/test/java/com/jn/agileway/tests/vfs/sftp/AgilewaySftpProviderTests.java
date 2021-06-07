package com.jn.agileway.tests.vfs.sftp;

import com.jn.agileway.vfs.provider.AgilewayVFSManagerBootstrap;
import com.jn.agileway.vfs.provider.sftp.SftpFileSystemConfigBuilder;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.Strings;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
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

        String url = "sftp://fangjinuo:fjn13570@192.168.1.70:22/test2/vfs_sftp_test";


        FileSystemOptions fileSystemOptions = new FileSystemOptions();
        SftpFileSystemConfigBuilder configBuilder = SftpFileSystemConfigBuilder.getInstance();
        configBuilder.setUserDirIsRoot(fileSystemOptions, true);

        FileObject fileObject = fileSystemManager.resolveFile(url, fileSystemOptions);
        showFile(0, fileObject);
        System.out.println("=============================");


        url ="file://d:/tmp002";
        FileObject localFileObject = fileSystemManager.resolveFile(url);
        if(fileObject.isFolder()) {
            if (!localFileObject.exists()) {
                localFileObject.createFolder();
            } else {
                localFileObject.delete(Selectors.EXCLUDE_SELF);
            }
            localFileObject.copyFrom(fileObject, Selectors.EXCLUDE_SELF);
        }else{
            // 单独测试 文件时，将上面的 url 改成一个 文件的url即可
            long writeSize= fileObject.getContent().write(localFileObject);
            long expectedSize= fileObject.getContent().getSize();
            Preconditions.checkTrue(writeSize==expectedSize);
            // localFileObject.copyFrom(fileObject, Selectors.SELECT_SELF);
        }


        System.out.println("=============================");
        url = "sftp://fangjinuo:fjn13570@192.168.1.70:22/test2/vfs_sftp_test2";
        fileObject = fileSystemManager.resolveFile(url, fileSystemOptions);
        if(!fileObject.exists()){
            fileObject.createFolder();
        }else{
            fileObject.delete(Selectors.EXCLUDE_SELF);
        }
        fileObject.copyFrom(localFileObject, Selectors.SELECT_ALL);

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
