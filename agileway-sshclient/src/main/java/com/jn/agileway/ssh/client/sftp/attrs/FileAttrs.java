package com.jn.agileway.ssh.client.sftp.attrs;

import com.jn.langx.util.Dates;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.io.file.FilePermission;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class FileAttrs {
    private Long size = null;

    public Long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    private Integer uid = null;

    public Integer getUid() {
        return this.uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    private Integer gid = null;

    public Integer getGid() {
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

    /**
     * access time: unit: s
     */
    private Integer atime = null;

    public Integer getAccessTime() {
        return this.atime;
    }

    public void setAccessTime(int accessTime) {
        this.atime = accessTime;
    }


    /**
     * modify time: unit: s
     */
    private Integer mtime = null;

    public Integer getModifyTime() {
        return this.mtime;
    }

    public void setModifyTime(int modifyTime) {
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


    public boolean isDirectory() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.DIRECTORY;
        }
        return false;
    }

    public boolean isFile() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.REGULAR;
        }
        return false;
    }


    public boolean isSocket() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.SOCKET_SPECIAL;
        }
        return false;
    }

    public boolean isBlock() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.BLOCK_SPECIAL;
        }
        return false;
    }

    public boolean isChar() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.CHAR_SPECIAL;
        }
        return false;
    }

    public boolean isLink() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.SYMBOLIC_LINK;
        }
        return false;
    }

    public boolean isFIFO() {
        if (this.fileMode != null) {
            return this.fileMode.getType() == FileType.FIFO_SPECIAL;
        }
        return false;
    }

    public FileType getFileType() {
        if (this.fileMode != null) {
            return this.fileMode.getType();
        }
        return FileType.UNKNOWN;
    }

    @Override
    public String toString() {
        // https://www.cnblogs.com/gezp/p/12875219.html
        StringBuilder builder = new StringBuilder(256);
        if (fileMode != null) {
            builder.append(fileMode.getType().getDisplayText());
            Set<FilePermission> filePermissions = fileMode.getPermissions();
            builder.append(filePermissions.contains(FilePermission.USR_R) ? "r" : "-");
            builder.append(filePermissions.contains(FilePermission.USR_W) ? "w" : "-");
            builder.append(filePermissions.contains(FilePermission.USR_X) ? "x" : "-");
            builder.append(filePermissions.contains(FilePermission.GRP_R) ? "r" : "-");
            builder.append(filePermissions.contains(FilePermission.GRP_W) ? "w" : "-");
            builder.append(filePermissions.contains(FilePermission.GRP_X) ? "x" : "-");
            builder.append(filePermissions.contains(FilePermission.OTH_R) ? "r" : "-");
            builder.append(filePermissions.contains(FilePermission.OTH_W) ? "w" : "-");
            builder.append(filePermissions.contains(FilePermission.OTH_X) ? "x" : "-");
            builder.append(" ");
        }

        if (this.size != null) {
            builder.append(this.size).append(" ");
        }

        if (this.mtime != null) {
            builder.append(Dates.format(new Date(this.mtime * 1000L), Dates.yyyy_MM_dd_HH_mm_ss));

        }

        return builder.toString();
    }
}
