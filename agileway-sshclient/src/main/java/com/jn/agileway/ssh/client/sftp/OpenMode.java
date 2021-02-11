package com.jn.agileway.ssh.client.sftp;

import com.jn.langx.util.enums.base.CommonEnum;
import com.jn.langx.util.enums.base.EnumDelegate;

/**
 * http://www.cplusplus.com/reference/cstdio/fopen/
 *
 * <pre>
 *                                (是否必须存在）
 *                                 EXIST   |  TRUNCATE CREATE APPEND WRITE READ
 *     READ  (r )                      1   |         0      0      0     0    1   => 00001  => 00000001 => 0x01
 *     WRITE (w )                      0   |         1      1      0     1    0   => 11010  => 00011010 => 0x1a
 *     APPEND(a )                      0   |         0      1      1     1    0   => 01110  => 00001110 => 0x0e
 *                                         |
 *     READ_PLUS (r+)                  1   |         0      0      0     1    1   => 00011  => 00000011 => 0x03
 *     WRITE_PLUS(r+)                  0   |         1      1      0     1    1   => 11011  => 00011011 => 0x1b
 *     APPEND_PLUS(a+)                 0   |         0      1      1     1    1   => 01111  => 00001111 => 0x0f
 *
 *     CREATE 与 EXIST 是完全相反的。
 *     所以把前
 * </pre
 */
public enum OpenMode implements CommonEnum {
    /**
     * read (r):
     * Open file for input operations.
     * The stream is positioned at the beginning of the file.
     * The file must exist.
     */
    READ(0x01, "r", "read"),
    /**
     * write (w):
     * Create an empty file for output operations.
     * The stream is positioned at the beginning of the file.
     * If a file with the same name already exists, its contents are truncated and the file is treated as a new empty file.
     */
    WRITE(0x1a, "w", "write"),
    /**
     * append (a):
     * Open file for output at the end of a file.
     * Output operations always write data at the end of the file, expanding it.
     * The file is created if it does not exist.
     */
    APPEND(0x0e, "a", "append"),

    /**
     * read/write (w+):
     * Open a file for update (both for input and output).
     * The stream is positioned at the beginning of the file.
     * The file must exist.
     */
    READ_PLUS(0x03, "r+", "read plus"),

    /**
     * write/write (w+):
     * Create an empty file and open it for update (both for input and output).
     * The stream is positioned at the beginning of the file.
     * If a file with the same name already exists its contents are truncated and the file is treated as a new empty file.
     */
    WRITE_PLUS(0x1b, "w+", "write plus"),

    /**
     * append/update:
     * Open a file for update (both for input and output) with all output operations writing data at the end of the file.
     * Repositioning operations (fseek, fsetpos, rewind) affects the next input operations, but output operations move the position back to the end of file.
     * The file is created if it does not exist.
     */
    APPEND_PLUS(0x0f, "a+", "append plus");

    private EnumDelegate delegate;

    public static final int SSH_FXP_READ        = 0x00000001;
    public static final int SSH_FXP_WRITE       = 0x00000002;
    public static final int SSH_FXP_APPEND      = 0x00000004;
    public static final int SSH_FXP_CREATE      = 0x00000008;
    public static final int SSH_FXP_TRUNCATE    = 0x00000010;
    public static final int SSH_FXP_EXCL        = 0x00000020;

    OpenMode(int code, String name, String displayText) {
        this.delegate = new EnumDelegate(code, name, displayText);
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
