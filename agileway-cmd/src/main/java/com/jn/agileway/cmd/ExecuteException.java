package com.jn.agileway.cmd;

import java.io.IOException;

/**
 * An exception indicating that the executing a subprocesses failed.
 */
public class ExecuteException extends IOException {

    /**
     * Comment for {@code serialVersionUID}.
     */
    private static final long serialVersionUID = 3256443620654331699L;

    /**
     * The underlying cause of this exception.
     */
    private final Throwable cause;

    /**
     * The exit value returned by the failed process
     */
    private final int exitValue;

    /**
     * Construct a new exception with the specified detail message.
     *
     * @param message   The detail message
     * @param exitValue The exit value
     */
    public ExecuteException(final String message, final int exitValue) {
        super(message + " (Exit value: " + exitValue + ")");
        this.cause = null;
        this.exitValue = exitValue;
    }

    /**
     * Construct a new exception with the specified detail message and cause.
     *
     * @param message   The detail message
     * @param exitValue The exit value
     * @param cause     The underlying cause
     */
    public ExecuteException(final String message, final int exitValue, final Throwable cause) {
        super(message + " (Exit value: " + exitValue + ". Caused by " + cause + ")");
        this.cause = cause; // Two-argument version requires JDK 1.4 or later
        this.exitValue = exitValue;
    }

    /**
     * Return the underlying cause of this exception (if any).
     */
    @Override
    public synchronized Throwable getCause() {
        return this.cause;
    }

    /**
     * Gets the exit value returned by the failed process
     *
     * @return The exit value
     */
    public int getExitValue() {
        return exitValue;
    }
}
