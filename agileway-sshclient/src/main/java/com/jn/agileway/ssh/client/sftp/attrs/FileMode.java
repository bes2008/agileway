package com.jn.agileway.ssh.client.sftp.attrs;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

import java.util.Collections;
import java.util.Set;

public class FileMode {

    public static enum Type implements CommonEnum {
        /**
         * block special
         */
        BLOCK_SPECIAL(0060000, "BLOCK_DEVICE", "b"),
        /**
         * character special
         */
        CHAR_SPECIAL(0020000, "CHAR_DEVICE", "c"),
        /**
         * FIFO special
         */
        FIFO_SPECIAL(0010000, "FIFO", "p"),
        /**
         * socket special
         */
        SOCKET_SPECIAL(0140000, "SOCKET", "s"),
        /**
         * regular
         */
        REGULAR(0100000, "REGULAR", "-"),
        /**
         * directory
         */
        DIRECTORY(0040000, "DIRECTORY", "d"),
        /**
         * symbolic link
         */
        SYMBOLIC_LINK(0120000, "SYMBOLIC_LINK", "l"),
        /**
         * unknown
         */
        UNKNOWN(0, "UNKNOWN", "unknown");

        private final EnumDelegate delegate;

        Type(int val, String name, String displayText) {
            this.delegate = new EnumDelegate(val, name, displayText);
        }

        public static FileMode.Type fromMask(int mask) {
            for (Type t : FileMode.Type.values()) {
                if (t.getMask() == mask) {
                    return t;
                }
            }
            return UNKNOWN;
        }

        public int getMask() {
            return getCode();
        }


        @Override
        public int getCode() {
            return this.delegate.getCode();
        }

        @Override
        public String getName() {
            return this.delegate.getName();
        }

        @Override
        public String getDisplayText() {
            return this.delegate.getDisplayText();
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
