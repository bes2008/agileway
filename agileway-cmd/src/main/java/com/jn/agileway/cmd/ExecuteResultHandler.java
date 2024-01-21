package com.jn.agileway.cmd;

import java.util.Map;

/**
 * The callback handlers for the result of asynchronous process execution. When a
 * process is started asynchronously the callback provides you with the result of
 * the executed process, i.e. the exit value or an exception.
 *
 * @see CommandLineExecutor#execute(CommandLine, Map)
 */
public interface ExecuteResultHandler {

    /**
     * The asynchronous execution completed.
     *
     * @param exitValue the exit value of the sub-process
     */
    void onProcessComplete(int exitValue);

    /**
     * The asynchronous execution failed.
     *
     * @param e the {@code ExecuteException} containing the root cause
     */
    void onProcessFailed(ExecuteException e);
}
