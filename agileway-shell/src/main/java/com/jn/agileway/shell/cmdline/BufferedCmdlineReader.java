package com.jn.agileway.shell.cmdline;

import com.jn.agileway.shell.cmdline.script.FileCmdlineProvider;
import com.jn.agileway.shell.exception.ShellInterruptedException;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class BufferedCmdlineReader implements CmdlineReader {
    private BufferedReader reader;
    private String prompt = "> ";

    private boolean isInteractive;

    public BufferedCmdlineReader(BufferedReader reader, boolean isInteractive){
        this.reader = reader;
        this.isInteractive = isInteractive;
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public String[] readCmdline(){
        String line = null;
        try {
            if(isInteractive){
                System.out.print(this.prompt);
            }
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

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    private boolean lineEnd(String lineFragment) {
        return lineFragment.isEmpty() || lineFragment.endsWith(";") || !Strings.endsWith(lineFragment, "\\");
    }


    protected void handleUnfinishedMultipleLineCommand(String rawLine){
        Logger logger  = Loggers.getLogger(FileCmdlineProvider.class);
        logger.warn("unfinished command line: {}", rawLine);
    }

}
