package com.jn.agileway.test.util.os.hardware.cpu;

import com.jn.agileway.cmd.CommandLine;
import com.jn.agileway.cmd.DefaultCommandLineExecutor;
import com.jn.langx.exception.UnsupportedPlatformException;
import com.jn.langx.util.logging.Loggers;
import com.jn.langx.util.os.OS;
import com.jn.langx.util.struct.Holder;
import org.slf4j.Logger;


public class CPUs {
    private static Holder<String> cpuId;

    private CPUs() {
    }

    public static String getCpuId() {
        if (cpuId == null) {
            DefaultCommandLineExecutor executor = new DefaultCommandLineExecutor();
            CommandLine commandLine = null;
            GetCpuIdStreamHandler handler = null;
            if (OS.isFamilyWindows()) {
                commandLine = CommandLine.parse("wmic cpu get ProcessorId");
                handler = new WindowsGetCpuIdStreamHandler();
            } else if (OS.isFamilyUnix()) {
                commandLine = CommandLine.parse("sudo dmidecode -t 4 | grep ID");
                handler = new LinuxGetCpuIdStreamHandler();
            }
            final Logger logger = Loggers.getLogger(CPUs.class);
            if (commandLine != null) {
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
                cpuId = new Holder<String>(handler.getCpuId());
            } else {
                throw new UnsupportedPlatformException();
            }
        }
        return cpuId.get();
    }
}
