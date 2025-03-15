package com.jn.agileway.shell.terminal;

import java.io.InputStream;
import java.io.OutputStream;

public interface TerminalController {
    boolean isAnsiSupported();

    boolean setTitle(String title);

    boolean enableInsertMode(boolean enabled);

    boolean enableLineInputMode(boolean enabled);
    String getLastErrorMessage();
    InputStream getInputStream();
    OutputStream getErrorStream();
    OutputStream getOutputStream();

}
