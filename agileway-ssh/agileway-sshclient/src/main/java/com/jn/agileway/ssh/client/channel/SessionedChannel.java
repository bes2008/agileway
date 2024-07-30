package com.jn.agileway.ssh.client.channel;

import com.jn.agileway.ssh.client.SshException;
import com.jn.agileway.ssh.client.utils.PTYMode;
import com.jn.agileway.ssh.client.utils.Signal;

import java.io.InputStream;
import java.util.Map;

/**
 * https://datatracker.ietf.org/doc/rfc4254/?include_text=1
 * <p>
 * 在执行完 exec, subsystem, shell 方法后，必须执行 close方法，因为sessioned channel 是不可重用的。
 * 在执行exec, subsystem, shell 方法之前，pty, x11forwarding 最好不要执行多次
 * 在执行exec, subsystem, shell 方法之前，env 方法可以执行多次
 */
public interface SessionedChannel extends Channel {

    /**
     * pseudo-terminal 请求。 要在 {@link #exec(String)}, {@link #env(String, String)}, {@link #shell()}, {@link #subsystem(String)} 请求之前。
     * <p>
     * equality: pty(term, 0, 0, 0, 0, null)
     */
    void pty(String term) throws SshException;

    /**
     * 发起 pseudo-terminal 请求， 要在 {@link #exec(String)}, {@link #env(String, String)}, {@link #subsystem(String)} 请求之前。
     *
     * @param term                 TERM environment variable value (e.g., vt100)
     * @param termWidthCharacters  terminal width, characters (e.g., 80)
     * @param termHeightCharacters terminal height, rows (e.g., 24)
     * @param termWidthPixels      terminal width, pixels (e.g., 640)
     * @param termHeightPixels     terminal height, pixels (e.g., 480)
     * @param terminalModes        encoded terminal modes
     */
    void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, Map<PTYMode, Integer> terminalModes) throws SshException;


    /**
     * 发送 X11 Forwarding 请求。 要在 {@link #exec(String)}, {@link #env(String, String)}, {@link #subsystem(String)} 请求之前。
     *
     * @param singleConnection          single connection
     * @param x11AuthenticationProtocol x11 authentication protocol
     * @param x11AuthenticationCookie   x11 authentication cookie
     * @param x11ScreenNumber           x11 screen number
     */
    void x11Forwarding(String hostname, int port, boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber) throws SshException;

    /**
     * 发起 env 请求， 用于设置环境变量。
     *
     * @param variableName
     * @param variableValue
     */
    void env(String variableName, String variableValue) throws SshException;

    /**
     * 发起 exec 请求，执行指定的命令，执行完毕后，应当关闭 session
     *
     * @param command
     */
    void exec(String command) throws SshException;

    /**
     * 发起 subsystem 请求，执行指定的 subsystem
     */
    void subsystem(String subsystem) throws SshException;

    /**
     * 发起 shell 请求，启动 shell 模式
     */
    ShellExecutor shell() throws SshException;


    void signal(Signal signal) throws SshException;

    /**
     * 在内部获取 exit status 时，时通过 ssh 命令来完成的，所以内部在获取值时，可能不在一个线程里，因此需要有个等待的过程。
     * <p>
     * 此外也不是所有的 ssh server 都会实现返回 exit status 的功能的，
     *
     * @return
     */
    int getExitStatus();

    /**
     * 远程机器错误输出的内容，会作为这里的错误输入
     *
     */
    InputStream getErrorInputStream() throws SshException;

}
