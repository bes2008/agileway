package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.*;
import java.util.Collection;
import java.util.List;

public class KnownHostsFiles {
    private static final Logger logger = Loggers.getLogger(KnownHostsFiles.class);

    public static List<HostsKeyEntry> read(byte[] knownHostsData) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(knownHostsData)));

        List<HostsKeyEntry> entries = Collects.emptyArrayList();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            // 注释行
            if (line.isEmpty() || line.startsWith("#")) {
                entries.add(new CommentHostsKeyEntry(line));
                continue;
            }
            List<String> segments = Collects.asList(line.split(" "));
            Marker marker = Enums.ofName(Marker.class, segments.get(0));
            if (marker != null) {
                segments = segments.subList(1, segments.size());
            }
            if (segments.size() >= 3) {
                HostKeyType keyType = Enums.ofName(HostKeyType.class, segments.get(1));
                String publicKeyBase64 = segments.get(2);

                if (keyType == null) {
                    byte[] pkBytes = Base64.decodeBase64(publicKeyBase64);

                    if (pkBytes[8] == 'd') {
                        keyType = HostKeyType.SSH_DSS;
                    } else if (pkBytes[8] == 'r') {
                        keyType = HostKeyType.SSH_RSA;
                    }
                    /*
                    else if (pkBytes[8] == 'a' && pkBytes[20] == '2') {
                        keyType = HostKeyType.ECDSA_SHA2_NISTP256;
                    } else if (pkBytes[8] == 'a' && pkBytes[20] == '3') {
                        keyType = HostKeyType.ECDSA_SHA2_NISTP384;
                    } else if (pkBytes[8] == 'a' && pkBytes[20] == '5') {
                        keyType = HostKeyType.ECDSA_SHA2_NISTP521;
                    }
                    */
                }

                if (keyType != null) {
                    String hosts = segments.get(0);
                    if (hosts.startsWith(HashedHostsKeyEntry.HOSTS_FLAG)) {
                        entries.add(new HashedHostsKeyEntry(marker, hosts, keyType, publicKeyBase64));
                    } else {
                        entries.add(new SimpleHostsKeyEntry(marker, hosts, keyType, publicKeyBase64));
                    }
                } else {
                    logger.warn("unsupported known_hosts key algorithm: {} ", segments.get(1));
                }
            } else {
                logger.warn("invalid known_hosts line: {} ", line);
            }
        }

        return entries;
    }

    public static List<HostsKeyEntry> read(File knownHosts) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(knownHosts);
            byte[] bytes = IOs.toByteArray(in);
            return read(bytes);
        } finally {
            IOs.close(in);
        }
    }

    public static void appendHostKeysToFile(File knownHosts, String[] hostnames, String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
        appendHostKeysToFile(knownHosts, new SimpleHostsKeyEntry(
                null,
                Strings.join(",", hostnames),
                Enums.ofName(HostKeyType.class, serverHostKeyAlgorithm),
                Base64.encodeBase64String(serverHostKey)
        ));
    }

    /**
     * Adds a single public key entry to the a known_hosts file.
     * This method is designed to be used in a {@link HostKeyVerifier}.
     *
     * @param knownHosts the file where the publickey entry will be appended.
     * @param entry      as passed to the {@link HostKeyVerifier}. base64 string
     * @throws IOException
     */
    public static void appendHostKeysToFile(File knownHosts, HostsKeyEntry entry) throws IOException {
        if (entry == null) {
            throw new IllegalArgumentException();
        }
        if (!entry.isValid() && !(entry instanceof CommentHostsKeyEntry)) {
            throw new IllegalArgumentException();
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(knownHosts, true);
            fileWriter.write(entry.getLine());
        } finally {
            IOs.close(fileWriter);
        }
    }

    public static void rewrite(File knownHosts, Collection<HostsKeyEntry> entries) throws IOException{
        BufferedWriter bos = null;
        try {
            Files.makeFile(knownHosts);
            bos = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(knownHosts)));
            for (HostsKeyEntry entry : entries) {
                bos.write(entry.getLine());
                bos.newLine();
            }
        } finally {
            IOs.close(bos);
        }
    }
}
