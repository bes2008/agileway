package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.ApplicationArgs;
import com.jn.agileway.shell.exception.ShellInterruptedException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.io.Charsets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class InputStreamCmdlineProvider implements CmdlineProvider{
    private ApplicationArgs appArgs;
    private boolean appArgsUsed = false;
    protected BufferedReader reader;

    public InputStreamCmdlineProvider(ApplicationArgs applicationArgs){
        this.appArgs = applicationArgs;
        this.reader = new BufferedReader(new InputStreamReader(System.in, Charsets.getDefault()));
    }

    @Override
    public String[] get() {
        if (!appArgsUsed && this.appArgs != null) {
            this.appArgsUsed = true;
            return this.appArgs.getArgs();
        }
        beforeRead();
        return next();
    }

    protected void beforeRead(){

    }

    protected String[] next(){
        String line = null;
        try {
            line = this.reader.readLine();
            line = line.trim();

            if (Strings.startsWith(line, "//")) {
                // this is a comment line, ignore it
                line = "";
            }
            if (Strings.startsWith(line, "/")) {
                line = Strings.substring(line, 1);
            }
            if (line.isEmpty()) {
                return ShellCmdlines.cmdlineToArgs(line);
            }
            if (line.endsWith(";") || !Strings.endsWith(line, "\\")) {
                return ShellCmdlines.cmdlineToArgs(line);
            }
            // 多行模式下
            StringBuilder buffer = new StringBuilder(255);
            buffer.append(Strings.substring(line, 0, line.length() - 1)).append(Strings.SPACE);

            line = this.reader.readLine();
            while (!lineEnd(line)) {
                // 进入这里时，必然是 以 \  结尾
                buffer.append(Strings.substring(line, 0, line.length() - 1)).append(Strings.SPACE);
                line = this.reader.readLine();
                if (Strings.startsWith(line, "//")) {
                    // this is a comment line
                    line = "";
                    break;
                }
            }
            buffer.append(line);
            String rawLine = buffer.toString();

            if (Strings.endsWith(rawLine, "\\")) {
                handleUnfinishedMultipleLineCommand(rawLine);
                return Emptys.EMPTY_STRINGS;
            }
            return ShellCmdlines.cmdlineToArgs(rawLine);
        } catch (IOException ioe) {
            throw new ShellInterruptedException(ioe.getMessage(), 128 + 20);
        }
    }

    protected void handleUnfinishedMultipleLineCommand(String rawLine){
    }

    private boolean lineEnd(String lineFragment) {
        return lineFragment.isEmpty() || lineFragment.endsWith(";") || !Strings.endsWith(lineFragment, "\\");
    }
}
