package com.jn.agileway.ssh.client.sftp.attrs;

import java.util.Collections;
import java.util.Set;

public class FileMode {

    public static enum Type {
        /** block special */
        BLOCK_SPECIAL(0060000),
        /** character special */
        CHAR_SPECIAL(0020000),
        /** FIFO special */
        FIFO_SPECIAL(0010000),
        /** socket special */
        SOCKET_SPECIAL(0140000),
        /** regular */
        REGULAR(0100000),
        /** directory */
        DIRECTORY(0040000),
        /** symbolic link */
        SYMBOLIC_LINK(0120000),
        /** unknown */
        UNKNOWN(0);

        private final int mask;

        private Type(int val) {
            this.mask = val;
        }

        public static FileMode.Type fromMask(int mask) {
            for (Type t : FileMode.Type.values()) {
                if (t.mask == mask) {
                    return t;
                }
            }
            return UNKNOWN;
        }

        public int getMask() {
            return mask;
        }

    }

    private final int mask;
    private final Type type;
    private final Set<FilePermission> perms;

    public FileMode(int mask) {
        this.mask = mask;
        this.type = Type.fromMask(getTypeMask());
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

    public Type getType() {
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
