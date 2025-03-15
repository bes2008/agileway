package com.jn.agileway.shell.terminal;


import com.jn.langx.text.StringTemplates;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.io.InputStream;
import java.io.OutputStream;

public class WindowsTerminalController implements TerminalController {
    private Kernel32 kernel32;

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
    private static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 0x0200;

    private WinNT.HANDLE terminalInputHandle;
    private WinNT.HANDLE terminalOutputHandle;
    private WinNT.HANDLE terminalErrorHandle;

    public WindowsTerminalController() {
        kernel32 = Kernel32.INSTANCE;

        terminalInputHandle =  kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
        if(WinBase.INVALID_HANDLE_VALUE.equals(terminalInputHandle)){
            throw new RuntimeException("Failed to get std-in handle");
        }
        terminalOutputHandle = kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
        if(WinBase.INVALID_HANDLE_VALUE.equals(terminalOutputHandle)){
            throw new RuntimeException("Failed to get std-out handle");
        }
        terminalErrorHandle = kernel32.GetStdHandle(Kernel32.STD_ERROR_HANDLE);
        if(WinBase.INVALID_HANDLE_VALUE.equals(terminalErrorHandle)){
            throw new RuntimeException("Failed to get std-err handle");
        }
    }

    @Override
    public boolean isAnsiSupported() {
        return false;
    }

    @Override
    public boolean setTitle(String title) {
        return Kernel32.INSTANCE.SetConsoleTitle(title);
    }

    @Override
    public boolean enableInsertMode(boolean enabled) {
        return enableMode(terminalInputHandle, ENABLE_INSERT_MODE, enabled);
    }

    @Override
    public boolean enableLineInputMode(boolean enabled) {
        return enableMode(terminalInputHandle, ENABLE_LINE_INPUT, enabled);
    }

    private boolean enableMode(WinNT.HANDLE stdHandle, int mode, boolean enabled) {
        int modes = getConsoleMode(stdHandle);
        if(modes == -1){
            return false;
        }
        modes = computeModes(modes, mode, enabled);
        return kernel32.SetConsoleMode(stdHandle, modes);
    }

    private int computeModes(int modes, int mode, boolean enabled) {
        if (enabled) {
            modes = modes | mode;
        } else {
            modes = modes & ~mode;
        }
        return modes;
    }

    private boolean setConsoleMode(WinNT.HANDLE stdHandle, int mode) {
        return kernel32.SetConsoleMode(stdHandle, mode);
    }

    private int getConsoleMode(WinNT.HANDLE stdHandle) {
        IntByReference mode = new IntByReference();
        boolean success = kernel32.GetConsoleMode(stdHandle, mode) ;
        if(!success){
            return -1;
        }
        return mode.getValue();
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
        return StringTemplates.formatWithPlaceholder("errorCode: {}, errorMessage: {}", kernel32.GetLastError(), Kernel32Util.getLastErrorMessage());
    }
}
