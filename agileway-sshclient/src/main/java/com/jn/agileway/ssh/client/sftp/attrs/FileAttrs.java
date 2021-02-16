package com.jn.agileway.ssh.client.sftp.attrs;

import com.jn.langx.util.collection.Collects;

import java.util.Map;
import java.util.Set;

public class FileAttrs {
    private long size = 0;

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    private int uid = 0;

    public int getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    private int gid;

    public int getGid() {
        return this.gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    private FileMode fileMode;

    public FileMode getFileMode() {
        return this.fileMode;
    }

    public void setFileMode(FileMode fileMode) {
        this.fileMode = fileMode;
    }

    private long atime;

    public long getAccessTime() {
        return this.atime;
    }

    public void setAccessTime(long accessTime) {
        this.atime = accessTime;
    }

    private long mtime;

    public long getModifyTime() {
        return this.mtime;
    }

    public void setModifyTime(long modifyTime) {
        this.mtime = modifyTime;
    }

    private Map<String, String> extendProperties = Collects.emptyHashMap();

    public String getExtend(String key) {
        return extendProperties.get(key);
    }

    public void setExtend(String key, String value) {
        this.extendProperties.put(key, value);
    }

    public Set<String> getExtendKeys() {
        return this.extendProperties.keySet();
    }
}
