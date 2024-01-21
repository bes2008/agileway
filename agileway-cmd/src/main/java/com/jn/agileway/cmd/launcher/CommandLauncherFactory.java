
package com.jn.agileway.cmd.launcher;

import com.jn.langx.util.os.OS;

/**
 * Builds a command launcher for the OS and JVM we are running under.
 */
public final class CommandLauncherFactory {

    private CommandLauncherFactory() {
    }

    /**
     * Factory method to create an appropriate launcher.
     *
     * @return the command launcher
     */
    public static CommandLauncher createVMLauncher() {
        // Try using a JDK 1.3 launcher
        CommandLauncher launcher;

        if (OS.isFamilyOpenVms()) {
            launcher = new VmsCommandLauncher();
        } else {
            launcher = new Java1_5CommandLauncher();
        }

        return launcher;
    }
}
