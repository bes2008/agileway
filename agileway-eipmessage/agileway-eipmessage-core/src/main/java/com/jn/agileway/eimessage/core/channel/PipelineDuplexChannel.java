package com.jn.agileway.eimessage.core.channel;

import com.jn.langx.pipeline.Pipeline;

public interface PipelineDuplexChannel extends DuplexChannel{
    void setPipeline(Pipeline pipeline);
    Pipeline getPipeline();
}
