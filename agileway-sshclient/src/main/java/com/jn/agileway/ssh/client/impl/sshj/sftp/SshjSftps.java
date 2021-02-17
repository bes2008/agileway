package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.ResponseStatusCode;
import com.jn.agileway.ssh.client.sftp.SftpResourceInfo;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.exception.NoSuchFileSftpException;
import com.jn.agileway.ssh.client.sftp.exception.SftpException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.function.Consumer;
import net.schmizz.sshj.sftp.*;

import java.util.Set;

class SshjSftps {

    public static SftpException wrapSftpException(SFTPException ex) {
        ResponseStatusCode statusCode = Enums.ofName(ResponseStatusCode.class, ex.getStatusCode().name());
        SftpException exception = null;
        if (statusCode == ResponseStatusCode.NO_SUCH_FILE) {
            exception = new NoSuchFileSftpException(ex);
        } else {
            exception = new SftpException(ex);
        }
        exception.setStatusCode(statusCode);
        return exception;
    }

    public static Set<OpenMode> toSshjOpenModeSet(int openMode) {
        Set<net.schmizz.sshj.sftp.OpenMode> openModes = Collects.emptyHashSet();
        if (openMode > 0) {
            if (com.jn.agileway.ssh.client.sftp.OpenMode.isReadable(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.READ);
            }
            if (com.jn.agileway.ssh.client.sftp.OpenMode.isWritable(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.WRITE);
            }
            if (com.jn.agileway.ssh.client.sftp.OpenMode.isCreatable(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.CREAT);
            }
            if (com.jn.agileway.ssh.client.sftp.OpenMode.isAppended(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.APPEND);
            }
            if (com.jn.agileway.ssh.client.sftp.OpenMode.isTruncated(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.TRUNC);
            }
            if (com.jn.agileway.ssh.client.sftp.OpenMode.willFailWhenCreateExist(openMode)) {
                openModes.add(net.schmizz.sshj.sftp.OpenMode.EXCL);
            }
        }
        return openModes;
    }

    public static FileAttributes toFileAttributes(final FileAttrs attrs) {
        FileAttributes attributes = FileAttributes.EMPTY;
        if (attrs != null) {
            final FileAttributes.Builder builder = new FileAttributes.Builder();
            if (attrs.getSize() != null) {
                builder.withSize(attrs.getSize());
            }

            com.jn.agileway.ssh.client.sftp.attrs.FileMode fileMode = attrs.getFileMode();
            if (fileMode != null) {
                builder.withType(FileMode.Type.fromMask(fileMode.getType().getMask()));
                builder.withPermissions(fileMode.getPermissionsMask());
            }

            if (attrs.getUid() != null || attrs.getGid() != null) {
                builder.withUIDGID(Objs.useValueIfNull(attrs.getUid(), 0), Objs.useValueIfNull(attrs.getGid(), 0));
            }

            if (attrs.getAccessTime() != null || attrs.getModifyTime() != null) {
                builder.withAtimeMtime(Objs.useValueIfNull(attrs.getAccessTime(), 0L), Objs.useValueIfNull(attrs.getModifyTime(), 0L));
            }

            Set<String> extendKeys = attrs.getExtendKeys();
            if (Emptys.isNotEmpty(extendKeys)) {
                Collects.forEach(extendKeys, new Consumer<String>() {
                    @Override
                    public void accept(String extendKey) {
                        builder.withExtended(extendKey, attrs.getExtend(extendKey));
                    }
                });
            }

            attributes = builder.build();
        }

        return attributes;
    }

    public static FileAttrs fromFileAttributes(FileAttributes attributes) {
        if (attributes == null) {
            return null;
        }
        FileAttrs attrs = new FileAttrs();

        if (attributes.getSize() != 0L) {
            attrs.setSize(attributes.getSize());
        }

        if (attributes.getAtime() != 0L) {
            attrs.setAccessTime(attributes.getAtime());
        }
        if (attributes.getMtime() != 0L) {
            attrs.setModifyTime(attributes.getMtime());
        }

        if (attributes.getUID() != 0L) {
            attrs.setUid(attributes.getUID());
        }
        if (attributes.getGID() != 0L) {
            attrs.setGid(attributes.getGID());
        }

        com.jn.agileway.ssh.client.sftp.attrs.FileMode fileMode = fromSshjFileMode(attributes.getMode());
        attrs.setFileMode(fileMode);

        return attrs;
    }

    public static com.jn.agileway.ssh.client.sftp.attrs.FileMode fromSshjFileMode(FileMode fileMode) {
        if (fileMode == null) {
            return null;
        }

        return new com.jn.agileway.ssh.client.sftp.attrs.FileMode(fileMode.getMask());
    }

    public static FileMode toSshjFileMode(com.jn.agileway.ssh.client.sftp.attrs.FileMode fileMode) {
        if (fileMode == null) {
            return null;
        }

        return new FileMode(fileMode.getMask());
    }

    public static SftpResourceInfo fromRemoteResourceInfo(RemoteResourceInfo info) {
        return new SftpResourceInfo(info.getPath(), fromFileAttributes(info.getAttributes()));
    }

}
