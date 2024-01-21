package com.jn.agileway.test.util.os.virtualization;

import com.jn.agileway.cmd.streamhandler.OutputExtractExecuteStreamHandler;

abstract class GetRuntimeContainerHandler extends OutputExtractExecuteStreamHandler {
    /**
     * 获取当前运行在的容器，不限定docker容器
     *
     */
    public abstract RuntimeContainer getContainer();

}
