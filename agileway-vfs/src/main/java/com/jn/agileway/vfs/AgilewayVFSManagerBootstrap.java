package com.jn.agileway.vfs;

import com.jn.agileway.vfs.provider.sftp.SftpFileProvider;
import com.jn.agileway.vfs.utils.VFSUtils;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;

public class AgilewayVFSManagerBootstrap {
    public static void startup() throws Throwable {
        FileSystemManager defaultFSManager = VFS.getManager();
        VFSUtils.addFileProvider((DefaultFileSystemManager) defaultFSManager, new SftpFileProvider(), "sftp");
    }
}
