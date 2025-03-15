package com.jn.agileway.shell.terminal;

public class Terminals {
    public static boolean isNativeTerminal() {
        return System.console() != null && !Boolean.getBoolean("terminal.emulated");
    }
}
