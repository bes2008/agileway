package com.jn.agileway.cmd.streamhandler;

import com.jn.agileway.cmd.ExecuteStreamHandler;
import com.jn.langx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractExecuteStreamHandler implements ExecuteStreamHandler {
    /**
     * 使用该 output，用于将数据写给子进程
     */
    protected OutputStream subProcessInput;
    /**
     * 使用该 input，用于读取 子进程的正常输出
     * <p>
     * 由于输出可以被重定向，所以该值有可能是 null
     */
    @Nullable
    protected InputStream subProcessOutputStream;

    /**
     * 使用该 input，用于读取 子进程的错误输出
     * <p>
     * 由于输出可以被重定向，所以该值有可能是 null
     */
    @Nullable
    protected InputStream subProcessErrorOutputStream;

    @Override
    public void setSubProcessInputStream(OutputStream os) throws IOException {
        this.subProcessInput = os;
    }

    @Override
    public void setSubProcessErrorStream(InputStream is) throws IOException {
        this.subProcessErrorOutputStream = is;
    }

    @Override
    public void setSubProcessOutputStream(InputStream is) throws IOException {
        this.subProcessOutputStream = is;
    }

    @Override
    public void stop() throws IOException {
        // 流关闭的事情由框架来完成
        subProcessInput = null;
        subProcessOutputStream = null;
        subProcessErrorOutputStream = null;
    }
}
