package com.jn.agileway.tests.vfs.sftp;

import com.jn.agileway.vfs.provider.VFSUtils;
import com.jn.agileway.vfs.provider.sftp.SftpFileProvider;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

public class AgilewaySftpProviderTests {
    public static void main(String[] args) throws Throwable{
        FileSystemManager defaultFSManager= VFS.getManager();
        System.out.println(defaultFSManager.hasProvider("sftp"));
        VFSUtils.addFileProvider((DefaultFileSystemManager)defaultFSManager,new SftpFileProvider(),"sftp");
        System.out.println(1);
    }
}
