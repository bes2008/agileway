package com.jn.agileway.test.util.os.hardware.cpu;

import com.jn.agileway.cmd.streamhandler.OutputExtractExecuteStreamHandler;

abstract class GetCpuIdStreamHandler extends OutputExtractExecuteStreamHandler<String> {
    public abstract String getCpuId();
}
