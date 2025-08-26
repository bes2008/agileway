package com.jn.agileway.shell.cmdline;

import java.io.Closeable;

/**
 * @since 5.1.1
 */
public interface CmdlineReader extends Closeable {
    String[] readCmdline();

    void setPrompt(String prompt);


}
