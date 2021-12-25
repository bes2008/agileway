package com.jn.agileway.ssh.client.impl.jsch;

import com.jcraft.jsch.Logger;
import com.jn.langx.annotation.NonNull;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.logging.Level;
import com.jn.langx.util.logging.Loggers;

public class JschLoggerToSlf4jLogger implements Logger {
    private org.slf4j.Logger delegate = Loggers.getLogger(JschLoggerToSlf4jLogger.class);

    @Override
    public boolean isEnabled(int level) {
        return isEnabled(delegate, toLogLevel(level));
    }

    @Override
    public void log(int level, String message) {
        Loggers.log(delegate, toLogLevel(level), null, message);
    }

    private Level toLogLevel(int level) {
        if (level <= Logger.DEBUG) {
            return Level.DEBUG;
        }
        if (level == Logger.INFO) {
            return Level.INFO;
        }
        if (level == Logger.WARN) {
            return Level.WARN;
        }
        return Level.ERROR;
    }

    private static boolean isEnabled(@NonNull org.slf4j.Logger logger, @NonNull Level level) {
        Preconditions.checkNotNull(level);
        Preconditions.checkNotNull(logger);

        boolean enabled = false;
        switch (level) {
            case TRACE:
                enabled = logger.isTraceEnabled();
                break;
            case DEBUG:
                enabled = logger.isDebugEnabled();
                break;
            case INFO:
                enabled = logger.isInfoEnabled();
                break;
            case WARN:
                enabled = logger.isWarnEnabled();
                break;
            case ERROR:
            default:
                enabled = logger.isErrorEnabled();
                break;
        }
        return enabled;
    }
}
