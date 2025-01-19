package com.jn.agileway.shell.result;

import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.agileway.shell.exception.MalformedOptionValueException;
import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.langx.text.StringTemplates;

public final class CmdExecResultHandler {
    private CmdOutputTransformer stdoutTransformer = new JsonStyleOutputTransformer();

    public void handle(CmdExecResult execResult) {
        if (execResult.getExitCode() >= 0) {
            return;
        }
        Throwable err = execResult.getErr();
        if (err == null) {
            execResult.setExitCode(0);
            String stdout = stdoutTransformer.transform(execResult.getStdoutData());
            execResult.setStdout(stdout);
            return;
        }

        if (err instanceof NotFoundCommandException) {
            execResult.setExitCode(127);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("not found command: {}", err.getMessage()));
            return;
        }

        if (err instanceof MalformedCommandException) {
            execResult.setExitCode(2);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("malformed command: {}", err.getMessage()));
            return;
        }

        if (err instanceof MalformedOptionValueException) {
            execResult.setExitCode(2);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("malformed option value: {}", err.getMessage()));
            return;
        }

        // TODO 126：权限被拒绝或无法执行

        // TODO 1: 通用错误
        execResult.setExitCode(1);
        execResult.setStderr(StringTemplates.formatWithPlaceholder("error: {}", err.getMessage()));
    }
}
