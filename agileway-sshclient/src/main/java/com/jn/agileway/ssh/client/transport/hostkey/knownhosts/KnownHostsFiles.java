package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.agileway.ssh.client.transport.hostkey.verifier.HostKeyVerifier;
import com.jn.agileway.ssh.client.utils.Buffer;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.io.file.Files;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.*;
import java.security.PublicKey;
import java.util.Collection;
import java.util.List;

public class KnownHostsFiles {
    private static final Logger logger = Loggers.getLogger(KnownHostsFiles.class);

    public static HostsKeyEntry parseLine(String line) throws Buffer.BufferException {
        if (line == null) {
            return null;
        }
        line = line.trim();
        // 注释行
        if (line.isEmpty() || line.startsWith("#")) {
            return new CommentHostsKeyEntry(line);
        }
        List<String> segments = Collects.asList(line.split(" "));
        Marker marker = Enums.ofName(Marker.class, segments.get(0));
        if (marker != null) {
            segments = segments.subList(1, segments.size());
        }
        if (segments.size() >= 2) {
            String hosts = segments.get(0);
            String keyTypeString = segments.get(1);
            String publicKeyBase64 = null;
            if (segments.size() == 2) {
                if (segments.get(1).length() < 25) {
                    logger.warn("invalid known_hosts line: {} ", line);
                    return null;
                } else {
                    // 猜猜是不是 public key:
                    Buffer.PlainBuffer buff = new Buffer.PlainBuffer(Base64.decodeBase64(segments.get(1)));
                    keyTypeString = buff.readString();
                    if (buff.available() < 4) {
                        logger.warn("invalid known_hosts line: {} ", line);
                        return null;
                    } else {
                        publicKeyBase64 = segments.get(1);
                    }
                }
            } else {
                publicKeyBase64 = segments.get(2);
            }
            HostKeyType keyType = Enums.ofName(HostKeyType.class, keyTypeString);
            if (keyType == null) {
                logger.warn("unsupported ssh hosts key type: {} ", segments.get(1));
            }
            Object publicKey = null;
            try {
                publicKey = new Buffer.PlainBuffer(Base64.decodeBase64(publicKeyBase64)).readPublicKey();
            } catch (UnsupportedHostsKeyTypeException ex) {
                publicKey = publicKeyBase64;
            }
            if (hosts.startsWith(HashedHostsKeyEntry.HOSTS_FLAG)) {
                return new HashedHostsKeyEntry(marker, hosts, keyTypeString, publicKey);
            } else {
                return new SimpleHostsKeyEntry(marker, hosts, keyTypeString, publicKey);
            }
        } else {
            logger.warn("invalid known_hosts line: {} ", line);
            return null;
        }
    }

    public static List<HostsKeyEntry> read(byte[] knownHostsData) throws IOException {

        List<HostsKeyEntry> entries = Collects.emptyArrayList();
        List<String> lines = IOs.readLines(new ByteArrayInputStream(knownHostsData));
        for (String line : lines) {
            HostsKeyEntry hostsKeyEntry = parseLine(line);
            if (hostsKeyEntry != null) {
                entries.add(hostsKeyEntry);
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
        PublicKey publicKey = new Buffer.PlainBuffer(serverHostKey).readPublicKey();
        appendHostKeysToFile(knownHosts, new SimpleHostsKeyEntry(
                null,
                Strings.join(",", hostnames),
                serverHostKeyAlgorithm,
                publicKey
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

    public static void rewrite(File knownHosts, Collection<HostsKeyEntry> entries) throws IOException {
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
