package com.jn.agileway.ssh.client.transport.hostkey.knownhosts;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import com.jn.agileway.ssh.client.transport.hostkey.HostKeyType;
import com.jn.langx.codec.base64.Base64;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.enums.Enums;
import com.jn.langx.util.io.IOs;

import java.io.*;
import java.util.List;

public class KnownHostsFiles {

    public static List<KnownHostEntry> read(char[] knownHostsData) throws IOException {
        BufferedReader br = new BufferedReader(new CharArrayReader(knownHostsData));

        List<KnownHostEntry> entries = Collects.emptyArrayList();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            line = line.trim();
            if (line.startsWith("#")) {
                continue;
            }
            String[] arr = line.split(" ");
            if (arr.length >= 3) {
                HostKeyType keyType = Enums.ofName(HostKeyType.class, arr[1]);
                if (keyType != null) {
                    entries.add(new KnownHostEntry(arr[0], keyType, arr[2]));
                }
            }
        }

        return entries;
    }

    public static List<KnownHostEntry> read(File knownHosts) throws IOException {
        char[] buff = new char[512];
        CharArrayWriter cw = new CharArrayWriter();
        knownHosts.createNewFile();
        FileReader fr = new FileReader(knownHosts);
        while (true) {
            int len = fr.read(buff);
            if (len < 0) {
                break;
            }
            cw.write(buff, 0, len);
        }
        fr.close();
        return read(cw.toCharArray());
    }

    public static void appendHostKeysToFile(File knownHosts, String[] hostnames, String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
        appendHostKeysToFile(knownHosts, hostnames, serverHostKeyAlgorithm, Base64.encodeBase64String(serverHostKey));
    }

    /**
     * Adds a single public key entry to the a known_hosts file.
     * This method is designed to be used in a {@link ServerHostKeyVerifier}.
     *
     * @param knownHosts             the file where the publickey entry will be appended.
     * @param hostnames              a list of hostname patterns - at least one most be specified. Check out the
     *                               OpenSSH sshd man page for a description of the pattern matching algorithm.
     * @param serverHostKeyAlgorithm as passed to the {@link ServerHostKeyVerifier}.
     * @param serverHostKey          as passed to the {@link ServerHostKeyVerifier}. base64 string
     * @throws IOException
     */
    public static void appendHostKeysToFile(File knownHosts, String[] hostnames, String serverHostKeyAlgorithm, String serverHostKey) throws IOException {
        if (hostnames == null || hostnames.length == 0) {
            throw new IllegalArgumentException("Need at least one hostname specification");
        }

        if (serverHostKeyAlgorithm == null || serverHostKey == null) {
            throw new IllegalArgumentException();
        }

        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < hostnames.length; i++) {
            if (i != 0) {
                buffer.append(',');
            }
            buffer.append(hostnames[i]);
        }

        buffer.append(' ');
        buffer.append(serverHostKeyAlgorithm);
        buffer.append(' ');
        buffer.append(serverHostKey);
        buffer.append("\n");

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(knownHosts, true);
            fileWriter.write(buffer.toString());
        } finally {
            IOs.close(fileWriter);
        }

    }
}
