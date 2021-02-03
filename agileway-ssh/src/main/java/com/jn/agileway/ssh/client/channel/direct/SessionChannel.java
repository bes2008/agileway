package com.jn.agileway.ssh.client.channel.direct;

import com.jn.agileway.ssh.client.Channel;

import java.io.InputStream;

/**
 * https://datatracker.ietf.org/doc/rfc4254/?include_text=1
 */
public interface SessionChannel extends Channel {

    /**
     * 远程机器错误输出的内容，会作为这里的错误输入
     *
     * @return
     */
    InputStream getErrorInputStream();

    /**
     * 发起 exec 请求，执行指定的命令
     *
     * @param command
     */
    void exec(String command);

    /**
     * 发起 subsystem 请求，执行指定的 subsystem
     */
    void subsystem(String subsystem);

    /**
     * 发起 shell 请求
     */
    void shell();

    /**
     * pseudo-terminal request
     * <p>
     * equality: pty(term, 0, 0, 0, 0, null)
     */
    void pty(String term);

    /**
     * @param term                 TERM environment variable value (e.g., vt100)
     * @param termWidthCharacters  terminal width, characters (e.g., 80)
     * @param termHeightCharacters terminal height, rows (e.g., 24)
     * @param termWidthPixels      terminal width, pixels (e.g., 640)
     * @param termHeightPixels     terminal height, pixels (e.g., 480)
     * @param terminalModes        encoded terminal modes
     */
    void pty(String term, int termWidthCharacters, int termHeightCharacters, int termWidthPixels, int termHeightPixels, byte[] terminalModes);


    /**
     * 发送 X11 Forwarding
     *
     * @param singleConnection          single connection
     * @param x11AuthenticationProtocol x11 authentication protocol
     * @param x11AuthenticationCookie   x11 authentication cookie
     * @param x11ScreenNumber           x11 screen number
     */
    void x11Forwarding(boolean singleConnection, String x11AuthenticationProtocol, String x11AuthenticationCookie, int x11ScreenNumber);

    void setEnvVariable(String variableName, String variableValue);

    void signal(String signalName);

    int getExitStatus();

}
