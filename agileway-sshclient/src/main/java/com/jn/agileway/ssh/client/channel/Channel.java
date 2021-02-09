package com.jn.agileway.ssh.client.channel;

import com.jn.agileway.ssh.client.SshException;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Channel extends Closeable {
    String getType();

    /**
     * 远程机器输出的内容，会作为这里的标准输入
     *
     * @return
     */
    InputStream getInputStream() throws SshException;

    /**
     * 获取一个输出到远程机器的流
     *
     * @return
     */
    OutputStream getOutputStream() throws SshException;

}
