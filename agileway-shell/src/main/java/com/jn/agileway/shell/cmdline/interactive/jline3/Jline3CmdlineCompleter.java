package com.jn.agileway.shell.cmdline.interactive.jline3;

import com.jn.agileway.shell.command.CommandRegistry;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;

/**
 * @since 5.1.1
 */
public class Jline3CmdlineCompleter implements Completer {

    private CommandRegistry commandRegistry;

    public Jline3CmdlineCompleter(CommandRegistry commandRegistry) {
        this.commandRegistry = commandRegistry;
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        // 如果 line 是空，则列出所有命令
        // 如果 line 中命令名还没有输完，则列出所有命令名以 line 为前缀的命令
        // 如果 line 中命令名已经输入完，则列出所有以 line 为前缀的选项名
    }
}
