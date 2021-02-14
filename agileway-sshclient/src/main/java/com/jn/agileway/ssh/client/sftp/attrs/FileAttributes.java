package com.jn.agileway.ssh.client.sftp.attrs;

import java.util.Set;

public interface FileAttributes {
    long getSize();

    int getUid();

    int getGid();

    FileMode getFileMode();

    long getAccessTime();

    long getModifyTime();

    String getExtend(String key);

    Set<String> getExtendKeys();
}
