package com.jn.agileway.shell.result;

import com.jn.agileway.shell.cmdline.AnsiFontText;
import com.jn.agileway.shell.cmdline.Cmdline;
import com.jn.agileway.shell.exception.*;
import com.jn.langx.io.stream.StringBuilderWriter;
import com.jn.langx.text.StringTemplates;
import com.jn.langx.util.Maths;
import com.jn.langx.util.Throwables;
import com.jn.langx.util.collection.Arrs;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.list.FixedSizeList;

import java.io.PrintWriter;
import java.util.List;


public final class CmdlineExecResultHandler {
    private CmdOutputTransformer stdoutTransformer = new RawTextOutputTransformer();
    private List<String> stacktraceList = FixedSizeList.fixedSizeList(Lists.newLinkedListWithFill(10, ""), true);

    public void handle(CmdlineExecResult execResult) {
        preOutput(execResult);
        output(execResult);
    }

    private void output(CmdlineExecResult execResult) {

        String out = execResult.getExitCode() == 0 ? execResult.getStdout() :
                AnsiFontText.ofErrorMessage(execResult.getStderr())
                        .bold(true).toString();
        if (out != null) {
            System.out.println(out);
        }

    }

    private CmdOutputTransformer getCmdOutputTransformer(Cmdline cmdline) {
        if (cmdline == null) {
            return this.stdoutTransformer;
        }
        CmdOutputTransformer transformer = cmdline.getCommandDefinition().getOutputTransformer();
        if (transformer == null) {
            transformer = this.stdoutTransformer;
        }
        return transformer;
    }

    private void preOutput(CmdlineExecResult execResult) {
        if (execResult.getExitCode() >= 0) {
            return;
        }
        Throwable err = execResult.getErr();
        if (err == null) {
            execResult.setExitCode(0);
            String stdout = getCmdOutputTransformer(execResult.getCmdline()).transform(execResult.getStdoutData());
            execResult.setStdout(stdout);
            return;
        }

        if (handleShellInterruptedException(execResult, err)) {
            return;
        }

        if (err instanceof NotFoundCommandException) {
            recordStacktrace(err, false);
            execResult.setExitCode(127);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("not found command: {}", err.getMessage()));
            return;
        }

        if (err instanceof MalformedCommandException) {
            recordStacktrace(err, false);
            execResult.setExitCode(2);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("malformed command: {}", err.getMessage()));
            return;
        }

        if (err instanceof MalformedOptionValueException) {
            recordStacktrace(err, false);
            execResult.setExitCode(2);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("malformed command option value: {}", err.getMessage()));
            return;
        }

        if (err instanceof MalformedCommandArgumentsException) {
            recordStacktrace(err, false);
            execResult.setExitCode(2);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("malformed command argument value: {}", err.getMessage()));
            return;
        }

        // TODO 126：权限被拒绝或无法执行

        // TODO 1: 通用错误
        recordStacktrace(err, true);
        execResult.setExitCode(1);
        execResult.setStderr(StringTemplates.formatWithPlaceholder("error: {}", Throwables.getRootCause(err).getMessage()));
    }

    private boolean handleShellInterruptedException(CmdlineExecResult execResult, Throwable ex) {
        ShellInterruptedException sie = null;
        if (ex instanceof ShellInterruptedException) {
            sie = (ShellInterruptedException) ex;
        }
        if (sie == null) {
            Throwable rootCause = Throwables.getRootCause(ex);
            if (rootCause instanceof ShellInterruptedException) {
                sie = (ShellInterruptedException) rootCause;
            }
        }
        if (sie != null) {
            execResult.setExitCode(Maths.abs(sie.getExitCode()) % 256);
            execResult.setErr(sie);
            if (execResult.getExitCode() != 0) {
                execResult.setStderr(getCmdOutputTransformer(execResult.getCmdline()).transform(sie.getMessage()));
            } else {
                execResult.setStdout(getCmdOutputTransformer(execResult.getCmdline()).transform(sie.getMessage()));
            }
        }

        return sie != null;
    }

    private void recordStacktrace(Throwable throwable, boolean recordRoot) {
        StringBuilderWriter stringBuilderWriter = new StringBuilderWriter();
        PrintWriter printWriter = new PrintWriter(stringBuilderWriter);
        (recordRoot ? Throwables.getRootCause(throwable) : throwable).printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();

        stacktraceList.add(stringBuilderWriter.toString());
    }

    public String getStacktrace(int index) {
        int i = Arrs.toPositiveIndex(this.stacktraceList.size(), index);
        return this.stacktraceList.get(i);
    }

}
