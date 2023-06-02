package com.jn.agileway.ssh.test.sftp;

import com.jn.agileway.ssh.client.sftp.OpenMode;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Radixs;
import org.junit.Test;

public class OpenModeTests {
    @Test
    public void test() {
        showOpenMode(OpenMode.READ);
        showOpenMode(OpenMode.WRITE);
        showOpenMode(OpenMode.APPEND);

        showOpenMode(OpenMode.READ_PLUS);
        showOpenMode(OpenMode.WRITE_PLUS);
        showOpenMode(OpenMode.APPEND_PLUS);
    }

    private void showOpenMode(OpenMode openMode) {
        System.out.println(StringTemplates.formatWithPlaceholder("======={}=======", openMode.getName()));
        System.out.println(StringTemplates.formatWithPlaceholder("code: {}, HEX: 0x{}", openMode.getCode(), Radixs.toHex2(openMode.getCode())));
        System.out.println("truncated: " + openMode.isTruncated());
        System.out.println("create: " + openMode.isCreatable());
        System.out.println("append: " + openMode.isAppended());
        System.out.println("write: " + openMode.isWritable());
        System.out.println("read: " + openMode.isReadable());
    }

}
