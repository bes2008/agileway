package com.jn.agileway.cmd.streamhandler;

import com.jn.langx.util.struct.Holder;

public abstract class OutputExtractExecuteStreamHandler<O> extends AbstractExecuteStreamHandler {
    protected final Holder<O> outputContent = new Holder<O>();

    public O getOutputContent() {
        return outputContent.get();
    }
}
