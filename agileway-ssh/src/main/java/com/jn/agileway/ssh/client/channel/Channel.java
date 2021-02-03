package com.jn.agileway.ssh.client.channel;

import java.io.InputStream;
import java.io.OutputStream;

public interface Channel {
    String getType();

    boolean isStarted();

    boolean isStopped();

    void close();

    /**
     * 远程机器输出的内容，会作为这里的标准输入
     *
     * @return
     */
    InputStream getStdInputStream();

    /**
     * 获取一个输出到远程机器的流
     *
     * @return
     */
    OutputStream getStdOutputStream();

}
