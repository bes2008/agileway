package com.jn.agileway.ssh.client;

import com.jn.agileway.ssh.client.channel.Channel;
import com.jn.agileway.ssh.client.channel.SessionedChannel;
import com.jn.agileway.ssh.client.transport.verifier.HostKeyVerifier;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * 其实就是 connection
 */
public interface SshConnection<CONF extends SshConnectionConfig> extends Closeable {
    String getId();

    void setId(String id);

    /**
     * @return 服务端主机
     */
    String getHost();

    CONF getConfig();

    void setConfig(CONF config);

    /**
     * @return 服务端端口
     */
    int getPort();

    void addHostKeyVerifier(HostKeyVerifier hostKeyVerifier);


    void connect(String host, int port) throws SshException;

    void connect(InetAddress host, int port) throws SshException;

    void connect(InetAddress host, int port, InetAddress localAddr, int localPort) throws SshException;


    boolean isClosed();

    boolean isConnected();

    SshConnectionStatus getStatus();

    /**
     * After a successful connect, one has to authenticate oneself. This method
     * sends username and password to the server.
     * <p>
     * If the authentication phase is complete, <code>true</code> will be
     * returned. If the server does not accept the request (or if further
     * authentication steps are needed), <code>false</code> is returned and
     * one can retry either by using this or any other authentication method
     * (use the <code>getRemainingAuthMethods</code> method to get a list of
     * the remaining possible methods).
     * <p>
     * Often, password authentication is disabled, but users are not aware of
     * it. Many servers only offer "publickey" and "keyboard-interactive".
     * However, even though "keyboard-interactive" *feels* like password
     * authentication (e.g., when using the putty or openssh clients) it is
     * *not* the same mechanism.
     *
     * @param user     the user name
     * @param password the password
     * @return if the connection is now authenticated.
     * @throws IOException when error
     */
    boolean authenticateWithPassword(String user, String password) throws SshException;

    /**
     * After a successful connect, one has to authenticate oneself.
     * The authentication method "publickey" works by signing a challenge
     * sent by the server. The signature is either DSA or RSA based - it
     * just depends on the type of private key you specify, either a DSA
     * or RSA private key in PEM format. And yes, this is may seem to be a
     * little confusing, the method is called "publickey" in the SSH-2 protocol
     * specification, however since we need to generate a signature, you
     * actually have to supply a private key =).
     * <p>
     * The private key contained in the PEM file may also be encrypted ("Proc-Type: 4,ENCRYPTED").
     * The library supports DES-CBC and DES-EDE3-CBC encryption, as well
     * as the more exotic PEM encrpytions AES-128-CBC, AES-192-CBC and AES-256-CBC.
     * <p>
     * If the authentication phase is complete, <code>true</code> will be
     * returned. If the server does not accept the request (or if further
     * authentication steps are needed), <code>false</code> is returned and
     * one can retry either by using this or any other authentication method
     * (use the <code>getRemainingAuthMethods</code> method to get a list of
     * the remaining possible methods).
     * <p>
     * NOTE PUTTY USERS: Event though your key file may start with "-----BEGIN..."
     * it is not in the expected format. You have to convert it to the OpenSSH
     * key format by using the "puttygen" tool (can be downloaded from the Putty
     * website). Simply load your key and then use the "Conversions/Export OpenSSH key"
     * functionality to get a proper PEM file.
     *
     * @param user          A <code>String</code> holding the username.
     * @param pemPrivateKey A <code>char[]</code> containing a DSA or RSA private key of the
     *                      user in OpenSSH key format (PEM, you can't miss the
     *                      "-----BEGIN DSA PRIVATE KEY-----" or "-----BEGIN RSA PRIVATE KEY-----"
     *                      tag). The char array may contain linebreaks/linefeeds.
     * @param passphrase    If the PEM structure is encrypted ("Proc-Type: 4,ENCRYPTED") then
     *                      you must specify a password. Otherwise, this argument will be ignored
     *                      and can be set to <code>null</code>.
     * @return whether the connection is now authenticated.
     * @throws IOException when error
     */
    boolean authenticateWithPublicKey(String user, char[] pemPrivateKey, String passphrase) throws SshException;

    /**
     * A convenience wrapper function which reads in a private key (PEM format, either DSA or RSA)
     * and then calls <code>authenticateWithPublicKey(String, char[], String)</code>.
     * <p>
     * NOTE PUTTY USERS: Event though your key file may start with "-----BEGIN..."
     * it is not in the expected format. You have to convert it to the OpenSSH
     * key format by using the "puttygen" tool (can be downloaded from the Putty
     * website). Simply load your key and then use the "Conversions/Export OpenSSH key"
     * functionality to get a proper PEM file.
     *
     * @param user       A <code>String</code> holding the username.
     * @param pemFile    A <code>File</code> object pointing to a file containing a DSA or RSA
     *                   private key of the user in OpenSSH key format (PEM, you can't miss the
     *                   "-----BEGIN DSA PRIVATE KEY-----" or "-----BEGIN RSA PRIVATE KEY-----"
     *                   tag).
     * @param passphrase If the PEM file is encrypted then you must specify the password.
     *                   Otherwise, this argument will be ignored and can be set to <code>null</code>.
     * @return whether the connection is now authenticated.
     * @throws IOException when error
     */
    boolean authenticateWithPublicKey(String user, File pemFile, String passphrase) throws SshException;

    /**
     * 发起“session” message 给server 打开session channel,然后发起 相应的 请求（exec, shell, subsystem）
     * <p>
     * <p>
     * 参见：https://datatracker.ietf.org/doc/rfc4254/?include_text=1 第6.1节
     */
    SessionedChannel openSession() throws SshException;

    Channel openForwardChannel();
}
