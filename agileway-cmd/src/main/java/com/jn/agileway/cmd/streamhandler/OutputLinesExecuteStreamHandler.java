package com.jn.agileway.cmd.streamhandler;

import com.jn.langx.util.io.IOs;

import java.io.IOException;
import java.util.List;

public class OutputLinesExecuteStreamHandler extends OutputExtractExecuteStreamHandler<List<String>> {

    @Override
    public void start() throws IOException {
        List<String> output = IOs.readLines(this.subProcessOutputStream);
        this.outputContent.set(output);
    }
}
