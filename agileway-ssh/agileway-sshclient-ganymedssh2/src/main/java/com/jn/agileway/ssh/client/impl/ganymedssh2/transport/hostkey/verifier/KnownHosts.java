package com.jn.agileway.ssh.client.impl.ganymedssh2.transport.hostkey.verifier;

import ch.ethz.ssh2.ServerHostKeyVerifier;
import ch.ethz.ssh2.crypto.Base64;
import ch.ethz.ssh2.crypto.digest.Digest;
import ch.ethz.ssh2.crypto.digest.HMAC;
import ch.ethz.ssh2.crypto.digest.MD5;
import ch.ethz.ssh2.crypto.digest.SHA1;
import ch.ethz.ssh2.signature.DSAPublicKey;
import ch.ethz.ssh2.signature.DSASHA1Verify;
import ch.ethz.ssh2.signature.RSAPublicKey;
import ch.ethz.ssh2.signature.RSASHA1Verify;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.collection.Pipeline;
import com.jn.langx.util.function.Consumer;
import com.jn.langx.util.function.Function;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * The <code>KnownHosts</code> class is a handy tool to verify received server hostkeys
 * based on the information in <code>known_hosts</code> files (the ones used by OpenSSH).
 * <p>
 * It offers basically an in-memory database for known_hosts entries, as well as some
 * helper functions. Entries from a <code>known_hosts</code> file can be loaded at construction time.
 * It is also possible to add more keys later (e.g., one can parse different
 * <code>known_hosts<code> files).
 * <p>
 * It is a thread safe implementation, therefore, you need only to instantiate one
 * <code>KnownHosts</code> for your whole application.
 *
 * @author Christian Plattner, plattner@inf.ethz.ch
 * @version $Id: KnownHosts.java,v 1.5 2006/07/30 21:59:29 cplattne Exp $
 */

public class KnownHosts {
    public static final int HOSTKEY_IS_OK = 0;
    public static final int HOSTKEY_IS_NEW = 1;
    public static final int HOSTKEY_HAS_CHANGED = 2;

    class KnownHostsEntry {
        private String[] patterns;
        Object key;

        KnownHostsEntry(String[] patterns, Object key) {
            this.patterns = patterns;
            this.key = key;
        }
    }

    private LinkedList publicKeys = new LinkedList();

    public KnownHosts() {
    }

    public KnownHosts(char[] knownHostsData) throws IOException {
        initialize(knownHostsData);
    }

    public KnownHosts(File knownHosts) throws IOException {
        initialize(knownHosts);
    }

    /**
     * Adds a single public key entry to the database. Note: this will NOT add the public key
     * to any physical file (e.g., "~/.ssh/known_hosts") - use <code>addHostkeyToFile()</code> for that purpose.
     * This method is designed to be used in a {@link ServerHostKeyVerifier}.
     *
     * @param hostnames              a list of hostname patterns - at least one most be specified. Check out the
     *                               OpenSSH sshd man page for a description of the pattern matching algorithm.
     * @param serverHostKeyAlgorithm as passed to the {@link ServerHostKeyVerifier}.
     * @param serverHostKey          as passed to the {@link ServerHostKeyVerifier}.
     * @throws IOException
     */
    public void addHostkey(String hostnames[], String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
        if (hostnames == null)
            throw new IllegalArgumentException("hostnames may not be null");

        if ("ssh-rsa".equals(serverHostKeyAlgorithm)) {
            RSAPublicKey rpk = RSASHA1Verify.decodeSSHRSAPublicKey(serverHostKey);

            synchronized (publicKeys) {
                publicKeys.add(new KnownHostsEntry(hostnames, rpk));
            }
        } else if ("ssh-dss".equals(serverHostKeyAlgorithm)) {
            DSAPublicKey dpk = DSASHA1Verify.decodeSSHDSAPublicKey(serverHostKey);

            synchronized (publicKeys) {
                publicKeys.add(new KnownHostsEntry(hostnames, dpk));
            }
        } else {
            throw new IOException("Unknown host key type (" + serverHostKeyAlgorithm + ")");
        }
    }

    public void removeHostKeys(String[] hosts, final String keyType) {
        Collects.forEach(hosts, new Consumer<String>() {
            @Override
            public void accept(String host) {
                Iterator<KnownHostsEntry> iter = publicKeys.iterator();
                while (iter.hasNext()) {
                    KnownHostsEntry entry = iter.next();
                    if (hostnameMatches(entry.patterns, host)) {
                        Object key = entry.key;
                        if (key instanceof RSAPublicKey || key instanceof DSAPublicKey) {
                            String currentKeyType = key instanceof RSAPublicKey ? "ssh-rsa":"ssh-dss";
                            if(Objs.equals(currentKeyType, keyType)) {
                                String[] newPatterns = Pipeline.of(entry.patterns)
                                        .remove(host)
                                        .toArray(String[].class);
                                if (Emptys.isEmpty(newPatterns)) {
                                    // 此时需要移除
                                    iter.remove();
                                } else {
                                    entry.patterns = newPatterns;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * Parses the given known_hosts data and adds entries to the database.
     *
     * @param knownHostsData
     * @throws IOException
     */
    public void addHostkeys(char[] knownHostsData) throws IOException {
        initialize(knownHostsData);
    }

    /**
     * Parses the given known_hosts file and adds entries to the database.
     *
     * @param knownHosts
     * @throws IOException
     */
    public void addHostkeys(File knownHosts) throws IOException {
        initialize(knownHosts);
    }

    /**
     * Generate the hashed representation of the given hostname. Useful for adding entries
     * with hashed hostnames to a known_hosts file. (see -H option of OpenSSH key-gen).
     *
     * @param hostname
     * @return the hashed representation, e.g., "|1|cDhrv7zwEUV3k71CEPHnhHZezhA=|Xo+2y6rUXo2OIWRAYhBOIijbJMA="
     */
    public static String createHashedHostname(String hostname) {
        SHA1 sha1 = new SHA1();

        byte[] salt = new byte[sha1.getDigestLength()];

        new SecureRandom().nextBytes(salt);

        byte[] hash = hmacSha1Hash(salt, hostname);

        String base64_salt = new String(Base64.encode(salt));
        String base64_hash = new String(Base64.encode(hash));

        return "|1|" + base64_salt + "|" + base64_hash;
    }

    private static byte[] hmacSha1Hash(byte[] salt, String hostname) {
        SHA1 sha1 = new SHA1();

        if (salt.length != sha1.getDigestLength()) {
            throw new IllegalArgumentException("Salt has wrong length (" + salt.length + ")");
        }
        HMAC hmac = new HMAC(sha1, salt, salt.length);

        hmac.update(hostname.getBytes());

        byte[] dig = new byte[hmac.getDigestLength()];

        hmac.digest(dig);

        return dig;
    }

    private boolean checkHashed(String entry, String hostname) {
        if (!entry.startsWith("|1|")) {
            return false;
        }
        int delim_idx = entry.indexOf('|', 3);

        if (delim_idx == -1) {
            return false;
        }

        String salt_base64 = entry.substring(3, delim_idx);
        String hash_base64 = entry.substring(delim_idx + 1);

        byte[] salt = null;
        byte[] hash = null;

        try {
            salt = Base64.decode(salt_base64.toCharArray());
            hash = Base64.decode(hash_base64.toCharArray());
        } catch (IOException e) {
            return false;
        }

        SHA1 sha1 = new SHA1();

        if (salt.length != sha1.getDigestLength()) {
            return false;
        }

        byte[] dig = hmacSha1Hash(salt, hostname);

        for (int i = 0; i < dig.length; i++) {
            if (dig[i] != hash[i]) {
                return false;
            }
        }
        return true;
    }

    private int checkKey(String remoteHostname, Object remoteKey) {
        int result = HOSTKEY_IS_NEW;

        synchronized (publicKeys) {
            Iterator i = publicKeys.iterator();

            while (i.hasNext()) {
                KnownHostsEntry ke = (KnownHostsEntry) i.next();

                if (!hostnameMatches(ke.patterns, remoteHostname)) {
                    continue;
                }
                boolean res = matchKeys(ke.key, remoteKey);
                if (res) {
                    return HOSTKEY_IS_OK;
                }
                result = HOSTKEY_HAS_CHANGED;
            }
        }
        return result;
    }

    private List<Object> getAllKeys(final String hostname) {
        synchronized (publicKeys) {
            List<Object> keys = Pipeline.<KnownHostsEntry>of(publicKeys)
                    .filter(new Predicate<KnownHostsEntry>() {
                        @Override
                        public boolean test(KnownHostsEntry ke) {
                            return hostnameMatches(ke.patterns, hostname);
                        }
                    }).map(new Function<KnownHostsEntry,Object>() {
                        @Override
                        public Object apply(KnownHostsEntry ke) {
                            return ke.key;
                        }
                    }).asList();
            return keys;
        }
    }


    private final boolean hostnameMatches(String[] hostpatterns, String hostname) {
        boolean isMatch = false;
        boolean negate = false;
        hostname = hostname.toLowerCase();
        for (int k = 0; k < hostpatterns.length; k++) {
            if (hostpatterns[k] == null) {
                continue;
            }
            String pattern = null;
            /* In contrast to OpenSSH we also allow negated hash entries (as well as hashed
             * entries in lines with multiple entries).
             */
            if ((hostpatterns[k].length() > 0) && (hostpatterns[k].charAt(0) == '!')) {
                pattern = hostpatterns[k].substring(1);
                negate = true;
            } else {
                pattern = hostpatterns[k];
                negate = false;
            }
            /* Optimize, no need to check this entry */
            if ((isMatch) && !negate) {
                continue;
            }

            /* Now compare */
            if (pattern.charAt(0) == '|') {
                if (checkHashed(pattern, hostname)) {
                    if (negate) {
                        return false;
                    }
                    isMatch = true;
                }
            } else {
                pattern = pattern.toLowerCase();

                if ((pattern.indexOf('?') != -1) || (pattern.indexOf('*') != -1)) {
                    if (pseudoRegex(pattern.toCharArray(), 0, hostname.toCharArray(), 0)) {
                        if (negate) {
                            return false;
                        }
                        isMatch = true;
                    }
                } else if (pattern.compareTo(hostname) == 0) {
                    if (negate) {
                        return false;
                    }
                    isMatch = true;
                }
            }
        }
        return isMatch;
    }

    private void initialize(char[] knownHostsData) throws IOException {
        BufferedReader br = new BufferedReader(new CharArrayReader(knownHostsData));

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
                if ((arr[1].compareTo("ssh-rsa") == 0) || (arr[1].compareTo("ssh-dss") == 0)) {
                    String[] hostnames = arr[0].split(",");
                    byte[] msg = Base64.decode(arr[2].toCharArray());
                    addHostkey(hostnames, arr[1], msg);
                }
            }
        }
    }


    private void initialize(File knownHosts) throws IOException {
        char[] buff = new char[512];
        CharArrayWriter cw = new CharArrayWriter();
        boolean actionResult= knownHosts.createNewFile();
        if(!actionResult){
            Loggers.getLogger("create known_hosts failed");
        }
        FileReader fr = null;
        try {
            fr = new FileReader(knownHosts);
            while (true) {
                int len = fr.read(buff);
                if (len < 0) {
                    break;
                }
                cw.write(buff, 0, len);
            }
            initialize(cw.toCharArray());
        }finally {
            IOs.close(fr);
        }
    }

    private boolean matchKeys(Object key1, Object key2) {
        if ((key1 instanceof RSAPublicKey) && (key2 instanceof RSAPublicKey)) {
            RSAPublicKey savedRSAKey = (RSAPublicKey) key1;
            RSAPublicKey remoteRSAKey = (RSAPublicKey) key2;
            if (!savedRSAKey.getE().equals(remoteRSAKey.getE())) {
                return false;
            }
            if (!savedRSAKey.getN().equals(remoteRSAKey.getN())) {
                return false;
            }
            return true;
        }

        if ((key1 instanceof DSAPublicKey) && (key2 instanceof DSAPublicKey)) {
            DSAPublicKey savedDSAKey = (DSAPublicKey) key1;
            DSAPublicKey remoteDSAKey = (DSAPublicKey) key2;

            if (!savedDSAKey.getG().equals(remoteDSAKey.getG())) {
                return false;
            }
            if (!savedDSAKey.getP().equals(remoteDSAKey.getP())) {
                return false;
            }
            if (!savedDSAKey.getQ().equals(remoteDSAKey.getQ())) {
                return false;
            }
            if (!savedDSAKey.getY().equals(remoteDSAKey.getY())) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean pseudoRegex(char[] pattern, int i, char[] match, int j) {
        /* This matching logic is equivalent to the one present in OpenSSH 4.1 */
        while (true) {
            /* Are we at the end of the pattern? */
            if (pattern.length == i) {
                return (match.length == j);
            }
            if (pattern[i] == '*') {
                i++;
                if (pattern.length == i) {
                    return true;
                }
                if ((pattern[i] != '*') && (pattern[i] != '?')) {
                    while (true) {
                        if ((pattern[i] == match[j]) && pseudoRegex(pattern, i + 1, match, j + 1)) {
                            return true;
                        }
                        j++;
                        if (match.length == j) {
                            return false;
                        }
                    }
                }
                while (true) {
                    if (pseudoRegex(pattern, i, match, j)) {
                        return true;
                    }
                    j++;
                    if (match.length == j) {
                        return false;
                    }
                }
            }
            if (match.length == j) {
                return false;
            }
            if ((pattern[i] != '?') && (pattern[i] != match[j])) {
                return false;
            }
            i++;
            j++;
        }
    }

    /**
     * Checks the internal hostkey database for the given hostkey.
     * If no matching key can be found, then the hostname is resolved to an IP address
     * and the search is repeated using that IP address.
     *
     * @param hostname               the server's hostname, will be matched with all hostname patterns
     * @param serverHostKeyAlgorithm type of hostkey, either <code>ssh-rsa</code> or <code>ssh-dss</code>
     * @param serverHostKey          the key blob
     * @return <ul>
     * <li><code>HOSTKEY_IS_OK</code>: the given hostkey matches an entry for the given hostname</li>
     * <li><code>HOSTKEY_IS_NEW</code>: no entries found for this hostname and this type of hostkey</li>
     * <li><code>HOSTKEY_HAS_CHANGED</code>: hostname is known, but with another key of the same type
     * (man-in-the-middle attack?)</li>
     * </ul>
     * @throws IOException if the supplied key blob cannot be parsed or does not match the given hostkey type.
     */
    public int verifyHostkey(String hostname, String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
        Object remoteKey = null;

        if ("ssh-rsa".equals(serverHostKeyAlgorithm)) {
            remoteKey = RSASHA1Verify.decodeSSHRSAPublicKey(serverHostKey);
        } else if ("ssh-dss".equals(serverHostKeyAlgorithm)) {
            remoteKey = DSASHA1Verify.decodeSSHDSAPublicKey(serverHostKey);
        } else {
            throw new IllegalArgumentException("Unknown hostkey type " + serverHostKeyAlgorithm);
        }
        int result = checkKey(hostname, remoteKey);
        if (result == HOSTKEY_IS_OK) {
            return result;
        }
        InetAddress[] ipAdresses = null;
        try {
            ipAdresses = InetAddress.getAllByName(hostname);
        } catch (UnknownHostException e) {
            return result;
        }
        for (int i = 0; i < ipAdresses.length; i++) {
            int newresult = checkKey(ipAdresses[i].getHostAddress(), remoteKey);
            if (newresult == HOSTKEY_IS_OK) {
                return newresult;
            }
            if (newresult == HOSTKEY_HAS_CHANGED) {
                result = HOSTKEY_HAS_CHANGED;
            }
        }

        return result;
    }

    /**
     * Adds a single public key entry to the a known_hosts file.
     * This method is designed to be used in a {@link ServerHostKeyVerifier}.
     *
     * @param knownHosts             the file where the publickey entry will be appended.
     * @param hostnames              a list of hostname patterns - at least one most be specified. Check out the
     *                               OpenSSH sshd man page for a description of the pattern matching algorithm.
     * @param serverHostKeyAlgorithm as passed to the {@link ServerHostKeyVerifier}.
     * @param serverHostKey          as passed to the {@link ServerHostKeyVerifier}.
     * @throws IOException
     */
    public static final void addHostKeyToFile(File knownHosts, String[] hostnames, String serverHostKeyAlgorithm, byte[] serverHostKey) throws IOException {
        if ((hostnames == null) || (hostnames.length == 0)) {
            throw new IllegalArgumentException("Need at least one hostname specification");
        }
        if ((serverHostKeyAlgorithm == null) || (serverHostKey == null)) {
            throw new IllegalArgumentException();
        }

        CharArrayWriter writer = new CharArrayWriter();
        for (int i = 0; i < hostnames.length; i++) {
            if (i != 0) {
                writer.write(',');
            }
            writer.write(hostnames[i]);
        }
        writer.write(' ');
        writer.write(serverHostKeyAlgorithm);
        writer.write(' ');
        writer.write(Base64.encode(serverHostKey));
        writer.write("\n");

        char[] entry = writer.toCharArray();
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(knownHosts, "rw");
            long len = raf.length();
            if (len > 0) {
                raf.seek(len - 1);
                int last = raf.read();
                if (last != '\n') {
                    raf.write('\n');
                }
            }
            raf.write(new String(entry).getBytes());
        }finally {
            IOs.close(raf);
        }

    }

    /**
     * Generates a "raw" fingerprint of a hostkey.
     *
     * @param type    either "md5" or "sha1"
     * @param keyType either "ssh-rsa" or "ssh-dss"
     * @param hostkey the hostkey
     * @return the raw fingerprint
     */
    private static byte[] rawFingerPrint(String type, String keyType, byte[] hostkey) {
        Digest dig = null;

        if ("md5".equals(type)) {
            dig = new MD5();
        } else if ("sha1".equals(type)) {
            dig = new SHA1();
        } else
            throw new IllegalArgumentException("Unknown hash type " + type);

        if ("ssh-rsa".equals(keyType)) {
        } else if ("ssh-dss".equals(keyType)) {
        } else
            throw new IllegalArgumentException("Unknown key type " + keyType);

        if (hostkey == null)
            throw new IllegalArgumentException("hostkey is null");

        dig.update(hostkey);
        byte[] res = new byte[dig.getDigestLength()];
        dig.digest(res);
        return res;
    }

    /**
     * Convert a raw fingerprint to hex representation (XX:YY:ZZ...).
     *
     * @param fingerprint raw fingerprint
     * @return the hex representation
     */
    static final private String rawToHexFingerprint(byte[] fingerprint) {
        final char[] alpha = "0123456789abcdef".toCharArray();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fingerprint.length; i++) {
            if (i != 0) {
                sb.append(':');
            }
            int b = fingerprint[i] & 0xff;
            sb.append(alpha[b >> 4]);
            sb.append(alpha[b & 15]);
        }

        return sb.toString();
    }

    /**
     * Convert a raw fingerprint to bubblebabble representation.
     *
     * @param raw raw fingerprint
     * @return the bubblebabble representation
     */
    private static String rawToBubblebabbleFingerprint(byte[] raw) {
        final char[] v = "aeiouy".toCharArray();
        final char[] c = "bcdfghklmnprstvzx".toCharArray();

        StringBuilder sb = new StringBuilder();
        int seed = 1;
        int rounds = (raw.length / 2) + 1;
        sb.append('x');
        for (int i = 0; i < rounds; i++) {
            if (((i + 1) < rounds) || ((raw.length) % 2 != 0)) {
                sb.append(v[(((raw[2 * i] >> 6) & 3) + seed) % 6]);
                sb.append(c[(raw[2 * i] >> 2) & 15]);
                sb.append(v[((raw[2 * i] & 3) + (seed / 6)) % 6]);

                if ((i + 1) < rounds) {
                    sb.append(c[(((raw[(2 * i) + 1])) >> 4) & 15]);
                    sb.append('-');
                    sb.append(c[(((raw[(2 * i) + 1]))) & 15]);
                    // As long as seed >= 0, seed will be >= 0 afterwords
                    seed = ((seed * 5) + (((raw[2 * i] & 0xff) * 7) + (raw[(2 * i) + 1] & 0xff))) % 36;
                }
            } else {
                sb.append(v[seed % 6]); // seed >= 0, therefore index positive
                sb.append('x');
                sb.append(v[seed / 6]);
            }
        }

        sb.append('x');

        return sb.toString();
    }

    /**
     * Convert a ssh2 key-blob into a human readable hex fingerprint.
     * Generated fingerprints are identical to those generated by OpenSSH.
     * <p>
     * Example fingerprint: d0:cb:76:19:99:5a:03:fc:73:10:70:93:f2:44:63:47.
     *
     * @param keytype   either "ssh-rsa" or "ssh-dss"
     * @param publickey key blob
     * @return Hex fingerprint
     */
    public static String createHexFingerprint(String keytype, byte[] publickey) {
        byte[] raw = rawFingerPrint("md5", keytype, publickey);
        return rawToHexFingerprint(raw);
    }

    /**
     * Convert a ssh2 key-blob into a human readable bubblebabble fingerprint.
     * The used bubblebabble algorithm (taken from OpenSSH) generates fingerprints
     * that are easier to remember for humans.
     * <p>
     * Example fingerprint: xofoc-bubuz-cazin-zufyl-pivuk-biduk-tacib-pybur-gonar-hotat-lyxux.
     *
     * @param keytype   either "ssh-rsa" or "ssh-dss"
     * @param publickey key data
     * @return Bubblebabble fingerprint
     */
    public static String createBubblebabbleFingerprint(String keytype, byte[] publickey) {
        byte[] raw = rawFingerPrint("sha1", keytype, publickey);
        return rawToBubblebabbleFingerprint(raw);
    }
}
