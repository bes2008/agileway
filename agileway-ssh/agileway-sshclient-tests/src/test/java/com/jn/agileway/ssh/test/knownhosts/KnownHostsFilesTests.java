package com.jn.agileway.ssh.test.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.HostsKeyEntry;
import com.jn.agileway.ssh.client.transport.hostkey.knownhosts.KnownHostsFiles;
import com.jn.langx.io.resource.Resource;
import com.jn.langx.io.resource.Resources;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.SystemPropertys;
import com.jn.langx.util.io.IOs;
import net.schmizz.sshj.transport.verification.OpenSSHKnownHosts;
import org.junit.jupiter.api.Test;

import java.io.File;
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
            System.out.println(entries);
        } finally {
            IOs.close(in);
        }
    }

    @Test
    public void test2() throws IOException {
        String workDir = SystemPropertys.getUserWorkDir();
        String filepath = StringTemplates.formatWithPlaceholder("{}/src/test/resources/known_hosts_test", workDir);
        File file = new File(filepath);
        InputStream in = null;
        try {
            OpenSSHKnownHosts knownHosts = new OpenSSHKnownHosts(file);
            System.out.println(knownHosts.entries());
        } finally {
            IOs.close(in);
        }
    }
}
