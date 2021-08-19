package com.jn.agileway.ssh.client.impl.trileadssh2.sftp;

import com.jn.agileway.ssh.client.sftp.ResponseStatusCode;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.enums.Enums;
import com.trilead.ssh2.SFTPException;
import com.trilead.ssh2.SFTPv3FileAttributes;

public class Ssh2Sftps {
    public static SftpException wrapSftpException(Throwable ex) {
        if (ex instanceof SFTPException) {
            ResponseStatusCode statusCode = Enums.ofCode(ResponseStatusCode.class, ((SFTPException) ex).getServerErrorCode());
            SftpException exception = null;
            if (statusCode == ResponseStatusCode.NO_SUCH_FILE) {
                exception = new NoSuchFileSftpException(ex);
            } else {
                exception = new SftpException(ex);
            }
            exception.setStatusCode(statusCode);
            return exception;
        } else {
            return new SftpException(ex);
        }
    }

    public static SFTPv3FileAttributes toSsh2FileAttributes(FileAttrs attributes) {
        if (attributes == null) {
            return null;
        }
        SFTPv3FileAttributes attrs = new SFTPv3FileAttributes();
        if (attributes.getSize() != null) {
            attrs.size = (attributes.getSize());
        }

        if (attributes.getAccessTime() != null) {
            attrs.atime = attributes.getAccessTime().longValue();
        }

        if (attributes.getModifyTime() != null) {
            attrs.mtime = attributes.getModifyTime().longValue();
        }

        if (attributes.getUid() != null) {
            attrs.uid = attributes.getUid();
        }

        if (attributes.getGid() != null) {
            attrs.gid = attributes.getGid();
        }

        if (attributes.getFileMode() != null) {
            attrs.permissions = attributes.getFileMode().getMask();
        }

        return attrs;
    }

    public static FileAttrs fromSsh2FileAttributes(SFTPv3FileAttributes attributes) {
        if (attributes == null) {
            return null;
        }

        FileAttrs attrs = new FileAttrs();
        if (attributes.size != null) {
            attrs.setSize(attributes.size);
        }

        if (attributes.atime != null) {
            attrs.setAccessTime(attributes.atime.intValue());
        }

        if (attributes.mtime != null) {
            attrs.setModifyTime(attributes.mtime.intValue());
        }

        if (attributes.uid != null) {
            attrs.setUid(attributes.uid);
        }

        if (attributes.gid != null) {
            attrs.setGid(attributes.gid);
        }

        if (attributes.permissions != null) {
            FileMode fileMode = new FileMode(attributes.permissions);
            attrs.setFileMode(fileMode);
        }

        return attrs;
    }
}
