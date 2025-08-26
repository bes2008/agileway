package com.jn.agileway.shell.result;

import com.jn.easyjson.core.util.JSONs;

public class JsonStyleOutputTransformer implements CmdOutputTransformer{
    @Override
    public String transform(Object methodResult) {
        return JSONs.toJson(methodResult,true,true);
    }
}
