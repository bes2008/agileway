package com.jn.agileway.shell.terminal;

import org.fusesource.jansi.internal.Kernel32;

import java.io.InputStream;
import java.io.OutputStream;

public class WindowsTerminalController implements TerminalController {

    /**
     * Input Buffers
     * <p>
     * https://learn.microsoft.com/en-us/windows/console/high-level-console-modes
     */
    private static final int ENABLE_PROCESSED_INPUT = 0x0001;
    private static final int ENABLE_LINE_INPUT = 0x0002;
    private static final int ENABLE_ECHO_INPUT = 0x0004;
    private static final int ENABLE_WINDOW_INPUT = 0x0008;
    private static final int ENABLE_MOUSE_INPUT = 0x0010;
    private static final int ENABLE_INSERT_MODE = 0x0020;
    private static final int ENABLE_QUIET_EDIT_MODE = 0x0040;
    private static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0001;

    private long terminalInputHandle;
    private long terminalOutputHandle;
    private long terminalErrorHandle;

    public WindowsTerminalController() {
        terminalInputHandle = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        terminalOutputHandle = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
        terminalErrorHandle = Kernel32.GetStdHandle(Kernel32.STD_ERROR_HANDLE);
    }

    @Override
    public boolean isAnsiSupported() {
        return false;
    }

    @Override
    public boolean setTitle(String title) {
        return Kernel32.SetConsoleTitle(title) == 0;
    }

    @Override
    public boolean enableInsertMode(boolean enabled) {
        return enableMode(terminalInputHandle, ENABLE_INSERT_MODE, enabled);
    }

    @Override
    public boolean enableLineInputMode(boolean enabled) {
        return enableMode(terminalInputHandle, ENABLE_LINE_INPUT, enabled);
    }

    private boolean enableMode(long stdHandle, int mode, boolean enabled) {
        int modes = getConsoleMode(stdHandle);
        if(modes == -1){
            return false;
        }
        modes = computeModes(modes, mode, enabled);
        return setConsoleMode(stdHandle, modes);
    }

    private int computeModes(int modes, int mode, boolean enabled) {
        if (enabled) {
            modes = modes | mode;
        } else {
            modes = modes & ~mode;
        }
        return modes;
    }

    private boolean setConsoleMode(long stdHandle, int mode) {
        return Kernel32.SetConsoleMode(stdHandle, mode) != 0;
    }

    private int getConsoleMode(long stdHandle) {
        int[] mode = new int[1];
        if (0 == Kernel32.GetConsoleMode(stdHandle, mode)) {
            return -1;
        }
        return mode[0];
    }

    @Override
    public InputStream getInputStream() {
        return System.in;
    }

    @Override
    public OutputStream getErrorStream() {
        return System.err;
    }

    @Override
    public OutputStream getOutputStream() {
        return System.out;
    }

    @Override
    public String getLastErrorMessage() {
        return Kernel32.getLastErrorMessage();
    }
}
