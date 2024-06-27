package com.jn.agileway.cmd;

import com.jn.agileway.cmd.launcher.AbstractLocalCommandLauncher;
import com.jn.agileway.cmd.launcher.CommandLauncher;
import com.jn.agileway.cmd.launcher.CommandLauncherFactory;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.io.IOs;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * The default class to start a subprocess. The implementation
 * allows to
 * <ul>
 *  <li>set a current working directory for the subprocess</li>
 *  <li>provide a set of environment variables passed to the subprocess</li>
 *  <li>capture the subprocess output of stdout and stderr using an ExecuteStreamHandler</li>
 *  <li>kill long-running processes using an ExecuteWatchdog</li>
 *  <li>define a set of expected exit values</li>
 *  <li>terminate any started processes when the main process is terminating using a ProcessDestroyer</li>
 * </ul>
 * <p>
 * The following example shows the basic usage:
 *
 * <pre>
 * Executor exec = new DefaultExecutor();
 * CommandLine cl = new CommandLine("ls -l");
 * int exitvalue = exec.execute(cl);
 * </pre>
 */
public class DefaultCommandLineExecutor implements CommandLineExecutor {
    private ExecutorService executorService;
    /**
     * taking care of output and error stream
     */
    private ExecuteStreamHandler streamHandler;

    private ExecuteResultHandler resultHandler;

    /**
     * the working directory of the process
     */
    private File workingDirectory;

    /**
     * monitoring of long running processes
     */
    private ExecuteWatchdog watchdog;

    /**
     * the exit values considered to be successful
     */
    private int[] exitValues;

    /**
     * launches the command in a new process
     */
    private CommandLauncher launcher;

    /**
     * optional cleanup of started processes
     */
    private InstructionSequenceDestroyer processDestroyer;


    /**
     * the first exception being caught to be thrown to the caller
     */
    private Throwable exceptionCaught;

    public ExecuteStreamHandler getStreamHandler() {
        return streamHandler;
    }

    public void setStreamHandler(ExecuteStreamHandler streamHandler) {
        if (streamHandler != null) {
            this.streamHandler = streamHandler;
        }
    }

    public ExecuteResultHandler getResultHandler() {
        return resultHandler;
    }

    public void setResultHandler(ExecuteResultHandler resultHandler) {
        if (resultHandler != null) {
            this.resultHandler = resultHandler;
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Default constructor creating a default {@code PumpStreamHandler}
     * and sets the working directory of the subprocess to the current
     * working directory.
     * <p>
     * The {@code PumpStreamHandler} pumps the output of the subprocess
     * into our {@code System.out} and {@code System.err} to avoid
     * into our {@code System.out} and {@code System.err} to avoid
     * a blocked or deadlocked subprocess (see{@link java.lang.Process Process}).
     */
    public DefaultCommandLineExecutor() {
        this.streamHandler = new PumpStreamHandler();
        this.resultHandler = new DefaultExecuteResultHandler();
        this.launcher = CommandLauncherFactory.createVMLauncher();
        this.exitValues = new int[0];
        this.workingDirectory = new File(".");
        this.exceptionCaught = null;
    }

    public void setLauncher(CommandLauncher launcher) {
        if (launcher != null) {
            this.launcher = launcher;
        }
    }

    /**
     * @see CommandLineExecutor#getWatchdog()
     */
    public ExecuteWatchdog getWatchdog() {
        return watchdog;
    }

    /**
     * @see CommandLineExecutor#setWatchdog(com.jn.agileway.cmd.ExecuteWatchdog)
     */
    public void setWatchdog(final ExecuteWatchdog watchDog) {
        this.watchdog = watchDog;
    }

    /**
     * @see CommandLineExecutor#getProcessDestroyer()
     */
    public InstructionSequenceDestroyer getProcessDestroyer() {
        return this.processDestroyer;
    }

    /**
     * @see CommandLineExecutor#setProcessDestroyer(InstructionSequenceDestroyer)
     */
    public void setProcessDestroyer(final InstructionSequenceDestroyer processDestroyer) {
        this.processDestroyer = processDestroyer;
    }

    /**
     * @see CommandLineExecutor#getWorkingDirectory()
     */
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * @see CommandLineExecutor#setWorkingDirectory(java.io.File)
     */
    public void setWorkingDirectory(final File dir) {
        this.workingDirectory = dir;
    }

    @Override
    public int execute(CommandLine command) throws IOException {
        return execute(false, command);
    }

    public int execute(boolean async, final CommandLine command) throws IOException {
        return execute(async, command, (Map<String, String>) null);
    }

    @Override
    public int execute(CommandLine command, Map<String, String> environment) throws IOException {
        return execute(false, command, environment);
    }

    public int execute(boolean async, final CommandLine command, final Map<String, String> environment) throws IOException {
        return execute(async, command, environment, null, null, null);
    }

    @Override
    public int execute(CommandLine command, ExecuteResultHandler handler) throws IOException {
        return execute(false, command, handler);
    }

    public int execute(boolean async, final CommandLine command, final ExecuteResultHandler handler) throws IOException {
        return execute(async, command, null, null, null, handler);
    }

    @Override
    public int execute(CommandLine command, Map<String, String> environment, File workingDirectory, ExecuteStreamHandler streamHandler, ExecuteResultHandler handler) throws IOException {
        return execute(false, command, environment, workingDirectory, streamHandler, handler);
    }

    public int execute(boolean async, final CommandLine command, final Map<String, String> environment, File workingDir, ExecuteStreamHandler executeStreamHandler, final ExecuteResultHandler resultHandler) throws IOException {
        if (watchdog != null) {
            watchdog.setProcessNotStarted();
        }
        final ExecuteStreamHandler streamHandler = Objs.useValueIfNull(executeStreamHandler, this.streamHandler);
        final File workingDirectory = Objs.useValueIfNull(workingDir, this.getWorkingDirectory());
        final ExecuteResultHandler executeResultHandler = Objs.useValueIfNull(resultHandler, this.resultHandler);
        Supplier<Integer> task = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return doExecute(command, environment, workingDirectory, streamHandler, executeResultHandler);
            }
        };
        CompletableFuture<Integer> future = executorService == null ? CompletableFuture.supplyAsync(task) : CompletableFuture.supplyAsync(task, executorService);

        if (!async) {
            try {
                return future.exceptionally(new Function<Throwable, Integer>() {
                    @Override
                    public Integer apply(Throwable ex) {
                        setExceptionCaught(ex);
                        return -1;
                    }
                }).get();
            } catch (Throwable ex) {
                throw Throwables.wrapAsRuntimeException(ex);
            }
        }
        return -1;
    }

    private int doExecute(final CommandLine command, final Map<String, String> environment, File workingDir, ExecuteStreamHandler executeStreamHandler, final ExecuteResultHandler resultHandler) {
        int exitValue = CommandLineExecutor.INVALID_EXITVALUE;
        try {
            exitValue = executeInternal(command, environment, workingDir, executeStreamHandler);
            resultHandler.onProcessComplete(exitValue);
        } catch (final ExecuteException e) {
            resultHandler.onProcessFailed(e);
        } catch (final Exception e) {
            resultHandler.onProcessFailed(new ExecuteException("Execution failed", exitValue, e));
        }

        return exitValue;
    }

    /**
     * @see CommandLineExecutor#setExitValue(int)
     */
    public void setExitValue(final int value) {
        this.setExitValues(new int[]{value});
    }


    /**
     * @see CommandLineExecutor#setExitValues(int[])
     */
    public void setExitValues(final int[] values) {
        this.exitValues = values == null ? null : values.clone();
    }

    /**
     * @see CommandLineExecutor#isFailure(int)
     */
    public boolean isFailure(final int exitValue) {

        if (this.exitValues == null) {
            return false;
        } else if (this.exitValues.length == 0) {
            return this.launcher.isFailure(exitValue);
        } else {
            for (final int exitValue2 : this.exitValues) {
                if (exitValue2 == exitValue) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Creates a process that runs a command.
     *
     * @param command    the command to run
     * @param env        the environment for the command
     * @param workingDir the working directory for the command
     * @return the process started
     * @throws IOException forwarded from the particular launcher used
     */
    protected InstructionSequence launch(final CommandLine command, final Map<String, String> env, final File workingDir) throws IOException {

        if (this.launcher == null) {
            throw new IllegalStateException("CommandLauncher can not be null");
        }
        if (workingDir == null) {
            throw new IOException(" the work directory is not exist.");
        }
        if (launcher instanceof AbstractLocalCommandLauncher) {
            if (!workingDir.exists()) {
                throw new IOException(workingDir + " doesn't exist.");
            }
        }
        return this.launcher.exec(command, env, workingDir);
    }


    /**
     * Close the streams belonging to the given Process.
     */
    private void closeProcessStreams(OutputStream processStdInput, InputStream processStdOutput, InputStream processStdError) {
        IOs.close(processStdInput);
        IOs.close(processStdOutput);
        IOs.close(processStdError);
    }

    /**
     * Execute an internal process. If the executing thread is interrupted while waiting for the
     * child process to return the child process will be killed.
     *
     * @param command       the command to execute
     * @param environment   the execution environment
     * @param dir           the working directory
     * @param streamHandler process the streams (in, out, err) of the process
     * @return the exit code of the process
     * @throws IOException executing the process failed
     */
    protected int executeInternal(final CommandLine command, final Map<String, String> environment, final File dir, final ExecuteStreamHandler streamHandler) throws IOException {

        setExceptionCaught(null);

        final InstructionSequence process = this.launch(command, environment, dir);

        InputStream subProcessStdOutput = null;
        InputStream subProcessStdError = null;
        OutputStream subProcessStdInput = null;

        try {
            subProcessStdOutput = process.getInputStream();
            subProcessStdError = process.getErrorStream();
            subProcessStdInput = process.getOutputStream();

            streamHandler.setSubProcessInputStream(subProcessStdInput);
            streamHandler.setSubProcessOutputStream(subProcessStdOutput);
            streamHandler.setSubProcessErrorStream(subProcessStdError);

            streamHandler.start();

            // add the process to the list of those to destroy if the VM exits
            if (this.getProcessDestroyer() != null) {
                this.getProcessDestroyer().add(process);
            }

            // associate the watchdog with the newly created process
            if (watchdog != null) {
                watchdog.start(process);
            }

            int exitValue = CommandLineExecutor.INVALID_EXITVALUE;

            try {
                exitValue = process.waitFor();
            } catch (final InterruptedException e) {
                process.destroy();
            } finally {
                // see http://bugs.sun.com/view_bug.do?bug_id=6420270
                // see https://issues.apache.org/jira/browse/EXEC-46
                // Process.waitFor should clear interrupt status when throwing InterruptedException
                // but we have to do that manually
                if (process instanceof ProcessAdapter) {
                    Thread.interrupted();
                }
            }

            if (watchdog != null) {
                watchdog.stop();
            }

            try {
                streamHandler.stop();
            } catch (final IOException e) {
                setExceptionCaught(e);
            }


            if (getExceptionCaught() != null) {
                throw Throwables.wrapAsRuntimeException(getExceptionCaught());
            }

            if (watchdog != null) {
                try {
                    watchdog.checkException();
                } catch (final IOException e) {
                    throw e;
                } catch (final Exception e) {
                    throw new IOException(e.getMessage());
                }
            }

            if (this.isFailure(exitValue)) {
                throw new ExecuteException("Process exited with an error: " + exitValue, exitValue);
            }

            return exitValue;
        } finally {
            closeProcessStreams(subProcessStdInput, subProcessStdOutput, subProcessStdError);
            Logger logger = Loggers.getLogger(getClass());
            // remove the process to the list of those to destroy if the VM exits
            if (this.getProcessDestroyer() != null) {
                try {
                    this.getProcessDestroyer().remove(process);
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            if (this.watchdog != null) {
                try {
                    this.watchdog.stop();
                } catch (Throwable ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
            try {
                process.destroy();
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Keep track of the first IOException being thrown.
     *
     * @param e the IOException
     */
    private void setExceptionCaught(final Throwable e) {
        if (this.exceptionCaught == null) {
            this.exceptionCaught = e;
        }
    }

    /**
     * Get the first IOException being thrown.
     *
     * @return the first IOException being caught
     */
    private Throwable getExceptionCaught() {
        return this.exceptionCaught;
    }

}
