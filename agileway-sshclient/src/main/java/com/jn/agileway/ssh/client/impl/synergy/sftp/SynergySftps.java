package com.jn.agileway.ssh.client.impl.synergy.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.langx.util.Numbers;
import com.sshtools.common.sftp.SftpFileAttributes;
import com.sshtools.common.util.UnsignedInteger32;
import com.sshtools.common.util.UnsignedInteger64;

class SynergySftps {

    public static int toOpenFlags(OpenMode openMode){
        return openMode.getCode();
    }

    public static FileAttrs fromSftpFileAttributes(SftpFileAttributes attributes) {
        if (attributes == null) {
            return null;
        }
        FileAttrs attrs = new FileAttrs();

        if (attributes.hasSize()) {
            attrs.setSize(attributes.getSize().longValue());
        }

        if (attributes.hasAccessTime()) {
            attrs.setAccessTime(attributes.getAccessedTime().bigIntValue().intValue());
        }
        if (attributes.hasModifiedTime()) {
            attrs.setModifyTime(attributes.getModifiedTime().bigIntValue().intValue());
        }

        if (attributes.hasUID()) {
            String uid = attributes.getUID();
            if (Numbers.isNumber(uid)) {
                attrs.setUid(Integer.parseInt(uid));
            }
        }
        if (attributes.hasGID()) {
            String gid = attributes.getGID();
            if (Numbers.isNumber(gid)) {
                attrs.setGid(Integer.parseInt(gid));
            }
        }

        FileType fileType = FileType.UNKNOWN;
        if (attributes.isFile()) {
            fileType = FileType.REGULAR;
        } else if (attributes.isDirectory()) {
            fileType = FileType.DIRECTORY;
        } else if (attributes.isBlock()) {
            fileType = FileType.BLOCK_SPECIAL;
        } else if (attributes.isCharacter()) {
            fileType = FileType.CHAR_SPECIAL;
        } else if (attributes.isFifo()) {
            fileType = FileType.FIFO_SPECIAL;
        } else if (attributes.isSocket()) {
            fileType = FileType.SOCKET_SPECIAL;
        } else if (attributes.isLink()) {
            fileType = FileType.SYMBOLIC_LINK;
        }

        int permissions = attributes.getPermissions().intValue();
        com.jn.agileway.ssh.client.sftp.attrs.FileMode fileMode = FileMode.createFileMode(fileType, permissions);
        attrs.setFileMode(fileMode);

        return attrs;
    }

    public static SftpFileAttributes toSftpFileAttributes(FileAttrs attrs, String encoding) {
        int fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_UNKNOWN;
        switch (attrs.getFileType()) {
            case REGULAR:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_REGULAR;
                break;
            case DIRECTORY:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_DIRECTORY;
                break;
            case SYMBOLIC_LINK:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_SYMLINK;
                break;
            case FIFO_SPECIAL:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_FIFO;
                break;
            case BLOCK_SPECIAL:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_BLOCK_DEVICE;
                break;
            case CHAR_SPECIAL:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_CHAR_DEVICE;
                break;
            case SOCKET_SPECIAL:
                fileType = SftpFileAttributes.SSH_FILEXFER_TYPE_SOCKET;
                break;
            default:
                break;
        }
        SftpFileAttributes attributes = new SftpFileAttributes(fileType, encoding);

        if (attrs.getSize() != null) {
            attributes.setSize(new UnsignedInteger64(attrs.getSize()));
        }

        UnsignedInteger64 atime = null;
        UnsignedInteger64 mtime = null;
        if (attrs.getAccessTime() != null) {
            atime = new UnsignedInteger64(attrs.getAccessTime());
        }
        if (attrs.getModifyTime() != null) {
            mtime = new UnsignedInteger64(attrs.getModifyTime());
        }
        attributes.setTimes(atime, mtime);

        if (attrs.getGid() != null) {
            attributes.setGID(attrs.getGid().toString());
        }
        if (attrs.getUid() != null) {
            attributes.setUID(attrs.getUid().toString());
        }


        FileMode fileMode = attrs.getFileMode();
        attributes.setPermissions(new UnsignedInteger32(fileMode.getPermissionsMask()));
        return attributes;
    }
}
