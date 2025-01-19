package com.jn.agileway.shell.result;

public class CmdExecResult {
    private int exitCode;

    /**
     * 命令从解析到执行，整个过程中发发生的异常
     */
    private Throwable err;
    /**
     * 命令从解析到执行，整个过程中发发生的异常，转换为stderr
     */
    private String stderr;

    /**
     * 要作为stdout输出的原始数据，对应的是cmd method的执行结果
     */
    private Object stdoutData;
    /**
     * 最终要放到stdout的内容
     */
    private String stdout;

    public Throwable getErr() {
        return err;
    }

    public void setErr(Throwable err) {
        this.err = err;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public Object getStdoutData() {
        return stdoutData;
    }

    public void setStdoutData(Object stdoutData) {
        this.stdoutData = stdoutData;
    }
}
