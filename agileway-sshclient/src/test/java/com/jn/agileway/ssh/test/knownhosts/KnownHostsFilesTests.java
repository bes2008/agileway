package com.jn.agileway.ssh.test.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.KnownHostsFiles;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.util.io.IOs;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class KnownHostsFilesTests {
    @Test
    public void test1() throws IOException {
        Resource resource = Resources.loadClassPathResource("/known_hosts_test");
        InputStream in = null;
        try {
            in = resource.getInputStream();
            byte[] bytes = IOs.toByteArray(in);
            List<HostsKeyEntry> entries = KnownHostsFiles.read(bytes);
        } finally {
            IOs.close(in);
        }
    }
}
