package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import org.junit.Assert;
import org.junit.Test;

public class FileModeTests {
    @Test
    public void test() {
        test0(FileType.REGULAR, 0644);
        test0(FileType.REGULAR, 0775);
    }

    void test0(FileType fileType, int permissions) {
        FileMode mode = FileMode.createFileMode(fileType, permissions);
        Assert.assertEquals(fileType, mode.getType());
        Assert.assertEquals(permissions, mode.getPermissionsMask());
    }
}
