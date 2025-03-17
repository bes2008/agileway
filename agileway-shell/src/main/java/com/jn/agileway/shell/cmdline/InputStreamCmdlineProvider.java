package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.langx.lifecycle.InitializationException;

import java.io.IOException;

public abstract class InputStreamCmdlineProvider extends AbstractCmdlineProvider{
    private ApplicationArgs appArgs;
    private boolean appArgsUsed = false;
    protected CmdlineReader reader;

    protected InputStreamCmdlineProvider(ApplicationArgs applicationArgs){
        this.appArgs = applicationArgs;
    }

    @Override
    protected final void doInit() throws InitializationException {
        initCmdlineReader();
    }

    protected abstract void initCmdlineReader();

    @Override
    protected String[] nextCmdline() {
        if (!appArgsUsed && this.appArgs != null) {
            this.appArgsUsed = true;
            return this.appArgs.getArgs();
        }
        beforeRead();
        return this.reader.readCmdline();
    }

    protected void beforeRead(){

    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }
}
