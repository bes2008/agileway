package com.jn.agileway.shell.cmdline.interactive.jline3;

import com.jn.agileway.shell.cmdline.CmdlineReader;
import com.jn.agileway.shell.cmdline.ShellCmdlines;
import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.langx.util.Throwables;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;

/**
 * @since 5.1.1
 */
public class Jline3CmdlineReader implements CmdlineReader {
    private LineReader lineReader;
    private String prompt;

    public Jline3CmdlineReader(CommandRegistry commandRegistry) {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).streams(System.in, System.out).jansi(true).type("jansi").build();
            lineReader = LineReaderBuilder.builder()
                    .terminal(terminal)
                    .appName("agileway-shell")
                    .completer(new Jline3CmdlineCompleter(commandRegistry))
                    .build();
        } catch (IOException e) {
            throw Throwables.wrapAsRuntimeException(e);
        }
    }

    @Override
    public String[] readCmdline() {
        String cmdline = lineReader.readLine(this.prompt);
        return ShellCmdlines.cmdlineToArgs(cmdline);
    }

    @Override
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    @Override
    public void close() throws IOException {

    }
}
