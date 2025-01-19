package com.jn.agileway.shell.result;

import com.jn.agileway.shell.exception.NotFoundCommandException;
import com.jn.langx.text.StringTemplates;

public final class CmdErrorHandler {
    public void handle(CmdExecResult execResult){
        if(execResult.getExitCode()>=0){
            return;
        }
        Throwable err = execResult.getErr();
        if(err==null){
            execResult.setExitCode(0);
        }

        if(err instanceof NotFoundCommandException){
            execResult.setExitCode(127);
            execResult.setStderr(StringTemplates.formatWithPlaceholder("not found command: {}", ((NotFoundCommandException)err).getMessage()));
        }


    }
}
