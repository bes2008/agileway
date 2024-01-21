package com.jn.agileway.cmd;

import java.util.Enumeration;
import java.util.Vector;

/**
 * Generalization of {@code ExecuteWatchdog}
 *
 * @see com.jn.agileway.cmd.ExecuteWatchdog
 */
public class Watchdog implements Runnable {

    private final Vector<TimeoutObserver> observers = new Vector<TimeoutObserver>(1);

    private final long timeout;

    private boolean stopped = false;

    public Watchdog(final long timeout) {
        if (timeout < 1) {
            throw new IllegalArgumentException("timeout must not be less than 1.");
        }
        this.timeout = timeout;
    }

    public void addTimeoutObserver(final TimeoutObserver to) {
        observers.addElement(to);
    }

    public void removeTimeoutObserver(final TimeoutObserver to) {
        observers.removeElement(to);
    }

    protected final void fireTimeoutOccured() {
        final Enumeration<TimeoutObserver> e = observers.elements();
        while (e.hasMoreElements()) {
            e.nextElement().onTimeout(this);
        }
    }

    public synchronized void start() {
        stopped = false;
        final Thread t = new Thread(this, "WATCHDOG");
        t.setDaemon(true);
        t.start();
    }

    public synchronized void stop() {
        stopped = true;
        notifyAll();
    }

    public void run() {
        final long startTime = System.currentTimeMillis();
        boolean isWaiting;
        synchronized (this) {
            long timeLeft = timeout - (System.currentTimeMillis() - startTime);
            isWaiting = timeLeft > 0;
            while (!stopped && isWaiting) {
                try {
                    wait(timeLeft);
                } catch (final InterruptedException e) {
                }
                timeLeft = timeout - (System.currentTimeMillis() - startTime);
                isWaiting = timeLeft > 0;
            }
        }

        // notify the listeners outside of the synchronized block (see EXEC-60)
        if (!isWaiting) {
            fireTimeoutOccured();
        }
    }

}
