package com.jn.agileway.shell.util;

import org.fusesource.jansi.AnsiConsole;

/**
 * 为了兼容 jansi 1.x 和 2.x
 */
public class AnsiTerminals {
    /**
     * The default mode which Jansi will use, can be either <code>force</code>, <code>strip</code>
     * or <code>default</code> (the default).
     * If this property is set, it will override <code>jansi.passthrough</code>,
     * <code>jansi.strip</code> and <code>jansi.force</code> properties.
     */
    public static final String JANSI_MODE = "jansi.mode";

    /**
     * Jansi mode value to strip all ansi sequences.
     */
    public static final String JANSI_MODE_STRIP = "strip";
    /**
     * Jansi mode value to force ansi sequences to the stream even if it's not a terminal.
     */
    public static final String JANSI_MODE_FORCE = "force";
    /**
     * Jansi mode value that output sequences if on a terminal, else strip them.
     */
    public static final String JANSI_MODE_DEFAULT = "default";


    private static int installed = 0;

    public static void install() {
        AnsiConsole.systemInstall();
        installed++;
    }

    public static void uninstall() {
        AnsiConsole.systemUninstall();
        installed--;
    }

    public static boolean isInstalled() {
        return installed > 0;
    }
}
