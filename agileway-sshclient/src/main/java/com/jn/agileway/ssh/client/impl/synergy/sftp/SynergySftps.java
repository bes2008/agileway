package com.jn.agileway.ssh.client.impl.synergy.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.agileway.ssh.client.sftp.attrs.FileMode;
import com.jn.agileway.ssh.client.sftp.attrs.FileType;
import com.jn.langx.util.Numbers;
import com.sshtools.common.sftp.SftpFileAttributes;

class SynergySftps {
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

    public static void toSftpFileAttributes(FileAttrs attrs) {
        //SftpFileAttributes attributes = new SftpFileAttributes();
    }
}
