package com.jn.agileway.shell.result;

import com.jn.agileway.shell.command.Command;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.text.StringTemplates;

public class CmdlineExecResult {
    /**
     * <pre>
     * <0： 代表未设置
     * 0： 正常退出
     * 1： 通用错误
     * 2： 命令或参数使用不当
     * 126：权限被拒绝或无法执行
     * 127：未找到命令
     * 128+n: 命令被信号从外部终止，或遇到致命错误
     * 130：通过 Ctrl+C 或 SIGINT 终止（终止代码 2 或键盘中断）
     * 143：通过 SIGTERM 终止（默认终止）
     * 255/*： 退出码超过了 0-255 的范围，因此重新计算（LCTT 译注：超过 255 后，用退出码对 256 取模）
     * </pre>
     */
    private int exitCode = -1;

    /**
     * 命令从解析到执行，整个过程中发发生的异常
     */
    private transient Throwable err;
    /**
     * 命令从解析到执行，整个过程中发发生的异常，转换为stderr。
     */
    private String stderr;

    /**
     * 要作为stdout输出的原始数据，对应的是cmd method的执行结果
     */
    private Object stdoutData;

    /**
     * 最终要放到stdout的内容
     * 调试时输出 stdoutData即可。
     */
    private transient String stdout;

    /**
     * 只有在读取命令行的过程失败了，cmdline 才会是 null，其它情况下 cmdline 肯定不能是null
     */
    @Nullable
    private transient String[] cmdline;
    /**
     * 只有关联到存在的命令定义时，才不会 是 null
     */
    @Nullable
    private transient Command command;

    public CmdlineExecResult(String[] cmdline) {
        this.cmdline = cmdline;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }


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
        this.exitCode = exitCode % 256;
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

    /**
     * 只用于调试
     */
    @Override
    public String toString() {
        return StringTemplates.formatWithPlaceholder("\texitCode: {}\n\tstdout: {}\n\tstderr: {}", exitCode, stdout, stderr);
    }
}
