package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.SftpATTRS;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Objs;

public class JschSftps {
    public static FileAttrs fromSftpATTRS(SftpATTRS sftpATTRS) {
        FileAttrs attrs = new FileAttrs();
        attrs.setSize(sftpATTRS.getSize());

        attrs.setUid(sftpATTRS.getUId());
        attrs.setGid(sftpATTRS.getGId());

        attrs.setAccessTime(sftpATTRS.getATime());
        attrs.setModifyTime(sftpATTRS.getMTime());

        FileType fileType = FileType.UNKNOWN;
        if (sftpATTRS.isBlk()) {
            fileType = FileType.BLOCK_SPECIAL;
        } else if (sftpATTRS.isChr()) {
            fileType = FileType.CHAR_SPECIAL;
        } else if (sftpATTRS.isFifo()) {
            fileType = FileType.FIFO_SPECIAL;
        } else if (sftpATTRS.isSock()) {
            fileType = FileType.SOCKET_SPECIAL;
        } else if (sftpATTRS.isLink()) {
            fileType = FileType.SYMBOLIC_LINK;
        } else if (sftpATTRS.isReg()) {
            fileType = FileType.REGULAR;
        } else if (sftpATTRS.isDir()) {
            fileType = FileType.DIRECTORY;
        }

        FileMode fileMode = new FileMode(sftpATTRS.getPermissions());
        attrs.setFileMode(fileMode);

        return attrs;
    }

    public static SftpATTRS toSftpATTRS(@NonNull SftpATTRS sftpATTRS, @NonNull FileAttrs attrs) {
        Long size = attrs.getSize();
        if (size != null) {
            sftpATTRS.setSIZE(size);
        }

        Integer uid = attrs.getUid();
        Integer gid = attrs.getGid();

        if (uid != null || gid != null) {
            sftpATTRS.setUIDGID(
                    Objs.useValueIfNull(uid, sftpATTRS.getUId()),
                    Objs.useValueIfNull(gid, sftpATTRS.getGId())
            );
        }

        Long atime = attrs.getAccessTime();
        Long mtime = attrs.getModifyTime();

        if (atime != null || mtime != null) {
            sftpATTRS.setACMODTIME(
                    Objs.useValueIfNull(atime, sftpATTRS.getATime()).intValue(),
                    Objs.useValueIfNull(mtime, sftpATTRS.getMTime()).intValue()
            );
        }

        FileMode fileMode = attrs.getFileMode();
        if (fileMode != null) {
            sftpATTRS.setPERMISSIONS(fileMode.getMask());
        }

        return sftpATTRS;
    }
}
