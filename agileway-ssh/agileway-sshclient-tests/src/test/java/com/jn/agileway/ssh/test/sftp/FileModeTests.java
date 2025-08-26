package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileModeTests {
    @Test
    public void test() {
        test0(FileType.REGULAR, 0644);
        test0(FileType.REGULAR, 0775);
    }

    void test0(FileType fileType, int permissions) {
        FileMode mode = FileMode.createFileMode(fileType, permissions);
        Assertions.assertEquals(fileType, mode.getType());
        Assertions.assertEquals(permissions, mode.getPermissionsMask());
    }
}
