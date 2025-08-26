package com.jn.agileway.shell.result;

public class RawTextOutputTransformer implements CmdOutputTransformer{
    @Override
    public String transform(Object methodResult) {
        return methodResult==null ?"":methodResult.toString();
    }
}
