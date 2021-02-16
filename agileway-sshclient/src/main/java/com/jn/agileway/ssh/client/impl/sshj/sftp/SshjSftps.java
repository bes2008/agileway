package com.jn.agileway.ssh.client.impl.sshj.sftp;

import com.jn.agileway.ssh.client.sftp.attrs.FileAttrs;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer;
import net.schmizz.sshj.sftp.FileAttributes;
import net.schmizz.sshj.sftp.FileMode;

import java.util.Set;

public class SshjSftps {

    public static FileAttributes toFileAttributes(final FileAttrs attrs) {
        FileAttributes attributes = FileAttributes.EMPTY;
        if (attrs != null) {
            final FileAttributes.Builder builder = new FileAttributes.Builder();
            if (attrs.getSize() > 0) {
                builder.withSize(attrs.getSize());
            }
            com.jn.agileway.ssh.client.sftp.attrs.FileMode fileMode = attrs.getFileMode();
            if (fileMode != null) {
                builder.withType(FileMode.Type.fromMask(fileMode.getType().getMask()));
                builder.withPermissions(fileMode.getPermissionsMask());
            }
            builder.withUIDGID(attrs.getUid(), attrs.getGid());
            builder.withAtimeMtime(attrs.getAccessTime(), attrs.getModifyTime());
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

        attrs.setAccessTime(attributes.getAtime());
        attrs.setModifyTime(attributes.getMtime());

        attrs.setUid(attributes.getUID());
        attrs.setGid(attributes.getGID());


        return attrs;
    }
}
