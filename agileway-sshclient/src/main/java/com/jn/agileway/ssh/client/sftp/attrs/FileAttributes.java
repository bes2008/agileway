package com.jn.agileway.ssh.client.sftp.attrs;

public interface FileAttributes {
    long getSize();
    int getUid();
    int getGid();
    FileMode getFileMode();
    long getAccessTime();
    long getModifyTime();
    String getExtend(String key);
}
