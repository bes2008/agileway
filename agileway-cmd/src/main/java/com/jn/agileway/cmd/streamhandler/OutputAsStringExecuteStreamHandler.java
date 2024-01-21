package com.jn.agileway.cmd.streamhandler;

import com.jn.langx.util.io.IOs;

import java.io.IOException;

public class OutputAsStringExecuteStreamHandler extends OutputExtractExecuteStreamHandler<String>{
    @Override
    public void start() throws IOException {
        String content = IOs.readAsString(this.subProcessOutputStream);
        this.outputContent.set(content);
    }
}
