package com.jn.agileway.cmd;

/**
 * Interface for classes that want to be notified by Watchdog.
 *
 * @see com.jn.agileway.cmd.Watchdog
 */
public interface TimeoutObserver {

    /**
     * Called when the watchdog times out.
     *
     * @param w the watchdog that timed out.
     */
    void onTimeout(Watchdog w);
}
