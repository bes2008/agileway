package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;

public class ScriptModeCmdlineProvider implements CmdlineProvider{
    private String filepath;
    public ScriptModeCmdlineProvider(ApplicationArgs applicationArgs){
        this.filepath = filepath;
    }
    @Override
    public String[] get() {
        return new String[0];
    }
}
