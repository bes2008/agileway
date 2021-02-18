package com.jn.agileway.ssh.client.sftp.attrs;

import java.util.Collections;
import java.util.Set;

public class FileMode {

    private final int mask;
    private final FileType type;
    private final Set<FilePermission> perms;

    public FileMode(int mask) {
        this.mask = mask;
        this.type = FileType.fromMask(getTypeMask());
        this.perms = FilePermission.fromMask(getPermissionsMask());
    }

    public int getMask() {
        return mask;
    }

    public int getTypeMask() {
        return mask & 0170000;
    }

    public int getPermissionsMask() {
        return mask & 07777;
    }

    public FileType getType() {
        return type;
    }

    public Set<FilePermission> getPermissions() {
        return Collections.unmodifiableSet(perms);
    }

    @Override
    public String toString() {
        return "[mask=" + Integer.toOctalString(mask) + "]";
    }

}
