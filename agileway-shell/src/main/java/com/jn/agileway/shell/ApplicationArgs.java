package com.jn.agileway.shell;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.EmptyEvalutible;
import com.jn.langx.util.Strings;

public class ApplicationArgs implements EmptyEvalutible {
    private String raw;
    private String[] args;

    public ApplicationArgs(@NonNull String[] args){
        this.args = args;
        this.raw = Strings.join(" ", args);
    }

    @Override
    public boolean isEmpty() {
        return this.args.length==0;
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public String getRaw() {
        return raw;
    }

    public String[] getArgs() {
        return args;
    }
}
