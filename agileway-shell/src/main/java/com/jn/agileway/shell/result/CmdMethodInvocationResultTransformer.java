package com.jn.agileway.shell.result;

import com.jn.langx.Transformer;

public interface CmdMethodInvocationResultTransformer extends Transformer<Object,String> {
    @Override
    String transform(Object methodResult);
}
