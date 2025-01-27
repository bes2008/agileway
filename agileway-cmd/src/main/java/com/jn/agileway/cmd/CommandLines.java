package com.jn.agileway.cmd;

import com.jn.agileway.cmd.streamhandler.OutputAsStringExecuteStreamHandler;
import com.jn.agileway.cmd.streamhandler.OutputLinesExecuteStreamHandler;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.annotation.Nullable;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;

public class CommandLines {

    private CommandLines() {
    }

    /**
     * 执行命令，并将输出内容以lines的形式返回
     */
    public static List<String> executeAndReadLines(@NonNull CommandLine commandLine, @Nullable File workDirectory, @Nullable Map<String, String> environment) {
        Preconditions.checkNotEmpty(commandLine, "the command line is null or empty");
        CommandLineExecutor executor = new DefaultCommandLineExecutor();
        if (workDirectory != null) {
            executor.setWorkingDirectory(workDirectory);
        }
        OutputLinesExecuteStreamHandler h = new OutputLinesExecuteStreamHandler();
        executor.setStreamHandler(h);
        try {
            executor.execute(commandLine, environment);
        } catch (Throwable ex) {
            Logger logger = Loggers.getLogger(CommandLines.class);
            logger.error(ex.getMessage(), ex);
        }
        return h.getOutputContent();
    }

    /**
     * 执行命令，并将输出内容以字符串的形式返回
     */
    public static String executeAndReadAsString(@NonNull CommandLine commandLine, @Nullable File workDirectory, @Nullable Map<String, String> environment) {
        Preconditions.checkNotEmpty(commandLine, "the command line is null or empty");
        CommandLineExecutor executor = new DefaultCommandLineExecutor();
        if (workDirectory != null) {
            executor.setWorkingDirectory(workDirectory);
        }
        OutputAsStringExecuteStreamHandler h = new OutputAsStringExecuteStreamHandler();
        executor.setStreamHandler(h);
        try {
            executor.execute(commandLine, environment);
        } catch (Throwable ex) {
            Logger logger = Loggers.getLogger(CommandLines.class);
            logger.error(ex.getMessage(), ex);
        }
        return h.getOutputContent();
    }
}
