package com.jn.agileway.shell;

import com.jn.langx.util.Strings;

public class ApplicationArgs {
    private String raw;
    private String[] args;

    public ApplicationArgs(String[] args){
        this.args = args;
        this.raw = Strings.join(" ", args);
    }

    public String getRaw() {
        return raw;
    }

    public String[] getArgs() {
        return args;
    }
}
