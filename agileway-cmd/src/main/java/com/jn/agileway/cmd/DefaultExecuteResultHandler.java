package com.jn.agileway.cmd;

/**
 * A default implementation of 'ExecuteResultHandler' used for asynchronous
 * process handling.
 */
public class DefaultExecuteResultHandler implements ExecuteResultHandler {

    /**
     * the interval polling the result
     */
    private static final int SLEEP_TIME_MS = 50;

    /**
     * Keep track if the process is still running
     */
    private volatile boolean hasResult;

    /**
     * The exit value of the finished process
     */
    private volatile int exitValue;

    /**
     * Any offending exception
     */
    private volatile ExecuteException exception;

    /**
     * Constructor.
     */
    public DefaultExecuteResultHandler() {
        this.hasResult = false;
        this.exitValue = CommandLineExecutor.INVALID_EXITVALUE;
    }

    /**
     * @see com.jn.agileway.cmd.ExecuteResultHandler#onProcessComplete(int)
     */
    public void onProcessComplete(final int exitValue) {
        this.exitValue = exitValue;
        this.exception = null;
        this.hasResult = true;
    }

    /**
     * @see com.jn.agileway.cmd.ExecuteResultHandler#onProcessFailed(com.jn.agileway.cmd.ExecuteException)
     */
    public void onProcessFailed(final ExecuteException e) {
        this.exitValue = e.getExitValue();
        this.exception = e;
        this.hasResult = true;
    }

    /**
     * Get the {@code exception} causing the process execution to fail.
     *
     * @return Returns the exception.
     * @throws IllegalStateException if the process has not exited yet
     */
    public ExecuteException getException() {

        if (!hasResult) {
            throw new IllegalStateException("The instruction sequence has not exited yet therefore no result is available ...");
        }

        return exception;
    }

    /**
     * Get the {@code exitValue} of the process.
     *
     * @return Returns the exitValue.
     * @throws IllegalStateException if the process has not exited yet
     */
    public int getExitValue() {

        if (!hasResult) {
            throw new IllegalStateException("The instruction sequence has not exited yet therefore no result is available ...");
        }

        return exitValue;
    }

    /**
     * Has the process exited and a result is available, i.e. exitCode or exception?
     *
     * @return true if a result of the execution is available
     */
    public boolean hasResult() {
        return hasResult;
    }

    /**
     * Causes the current thread to wait, if necessary, until the
     * process has terminated. This method returns immediately if
     * the process has already terminated. If the process has
     * not yet terminated, the calling thread will be blocked until the
     * process exits.
     *
     * @throws InterruptedException if the current thread is
     *                              {@linkplain Thread#interrupt() interrupted} by another
     *                              thread while it is waiting, then the wait is ended and
     *                              an {@link InterruptedException} is thrown.
     */
    public void waitFor() throws InterruptedException {

        while (!hasResult()) {
            Thread.sleep(SLEEP_TIME_MS);
        }
    }

    /**
     * Causes the current thread to wait, if necessary, until the
     * process has terminated. This method returns immediately if
     * the process has already terminated. If the process has
     * not yet terminated, the calling thread will be blocked until the
     * process exits.
     *
     * @param timeout the maximum time to wait in milliseconds
     * @throws InterruptedException if the current thread is
     *                              {@linkplain Thread#interrupt() interrupted} by another
     *                              thread while it is waiting, then the wait is ended and
     *                              an {@link InterruptedException} is thrown.
     */
    public void waitFor(final long timeout) throws InterruptedException {

        final long until = System.currentTimeMillis() + timeout;

        while (!hasResult() && System.currentTimeMillis() < until) {
            Thread.sleep(SLEEP_TIME_MS);
        }
    }
}