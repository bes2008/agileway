package com.jn.agileway.test.util.os.virtualization;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.DefaultCommandLineExecutor;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.os.OS;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;

public class RuntimeContainers {

    private static Holder<RuntimeContainer> runtimeContainer;

    public static RuntimeContainer getRuntimeContainer() {
        if (runtimeContainer == null) {
            DefaultCommandLineExecutor executor = new DefaultCommandLineExecutor();
            CommandLine commandLine = null;
            GetRuntimeContainerHandler handler = null;
            if (OS.isFamilyWindows()) {
                // commandLine = CommandLine.parse("wmic cpu get ProcessorId");
                handler = null;
            } else if (OS.isFamilyUnix()) {
                commandLine = CommandLine.parse("cat /proc/1/cpuset");
                handler = new LinuxGetRuntimeContainerHandler();
            }
            Logger logger = Loggers.getLogger(RuntimeContainers.class);
            if (commandLine != null && handler != null) {
                try {
                    executor.setStreamHandler(handler);
                    executor.execute(commandLine);
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } else {
                logger.error("Unsupported operation for current platform");
            }
            if (handler != null) {
                runtimeContainer = new Holder<RuntimeContainer>(handler.getContainer());
            }
        }

        return runtimeContainer == null? null:runtimeContainer.get();
    }

    private RuntimeContainers(){

    }
}
