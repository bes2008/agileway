package com.jn.agileway.ssh.client.impl.jsch.sftp;

import com.jcraft.jsch.SftpATTRS;
import com.jn.agileway.ssh.client.sftp.ResponseStatusCode;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Objs;
import com.jn.langx.util.enums.Enums;

public class JschSftps {

    public static SftpException wrapSftpException(com.jcraft.jsch.SftpException ex){
        ResponseStatusCode statusCode = Enums.ofCode(ResponseStatusCode.class, ex.id);
        SftpException exception = null;
        if (statusCode == ResponseStatusCode.NO_SUCH_FILE) {
            exception = new NoSuchFileSftpException(ex);
        } else {
            exception = new SftpException(ex);
        }
        exception.setStatusCode(statusCode);
        return exception;
    }

    public static FileAttrs fromSftpATTRS(SftpATTRS sftpATTRS) {
        FileAttrs attrs = new FileAttrs();
        attrs.setSize(sftpATTRS.getSize());

        attrs.setUid(sftpATTRS.getUId());
        attrs.setGid(sftpATTRS.getGId());

        attrs.setAccessTime(sftpATTRS.getATime());
        attrs.setModifyTime(sftpATTRS.getMTime());

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

        Integer atime = attrs.getAccessTime();
        Integer mtime = attrs.getModifyTime();

        if (atime != null || mtime != null) {
            sftpATTRS.setACMODTIME(
                    Objs.useValueIfNull(atime, sftpATTRS.getATime()),
                    Objs.useValueIfNull(mtime, sftpATTRS.getMTime())
            );
        }

        FileMode fileMode = attrs.getFileMode();
        if (fileMode != null) {
            sftpATTRS.setPERMISSIONS(fileMode.getMask());
        }

        return sftpATTRS;
    }
}
