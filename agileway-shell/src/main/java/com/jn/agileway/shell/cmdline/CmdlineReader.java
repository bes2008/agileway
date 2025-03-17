package com.jn.agileway.shell.cmdline;

import java.io.Closeable;

public interface CmdlineReader extends Closeable {
    String[] readCmdline();

    void setPrompt(String prompt);


}
