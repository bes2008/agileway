package com.jn.agileway.cmd;

import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

/**
 * Provides debugging support.
 *
 */
class CommandLineDebugs {
    private static final Logger logger = Loggers.getLogger(CommandLineDebugs.class);
    private CommandLineDebugs(){

    }
    /**
     * System property to determine how to handle exceptions. When
     * set to "false" we rethrow the otherwise silently catched
     * exceptions found in the original code. The default value
     * is "true"
     */
    public static final String COMMONS_EXEC_LENIENT = "com.jn.langx.commandline.lenient";

    /**
     * System property to determine how to dump an exception. When
     * set to "true" we print any exception to stderr. The default
     * value is "false"
     */
    public static final String COMMONS_EXEC_DEBUG = "com.jn.langx.commandline.debug";

    /**
     * Handles an exception based on the system properties.
     *
     * @param msg message describing the problem
     * @param e   an exception being handled
     */
    public static void handleException(final String msg, final Exception e) {

        if (isDebugEnabled()) {
            logger.error("error: {}", msg, e);
        }

        if (!isLenientEnabled()) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            // can't pass root cause since the constructor is not available on JDK 1.3
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Determines if debugging is enabled based on the
     * system property "COMMONS_EXEC_DEBUG".
     *
     * @return true if debug mode is enabled
     */
    public static boolean isDebugEnabled() {
        final String debug = System.getProperty(COMMONS_EXEC_DEBUG, Boolean.FALSE.toString());
        return Boolean.TRUE.toString().equalsIgnoreCase(debug);
    }

    /**
     * Determines if lenient mode is enabled.
     *
     * @return true if lenient mode is enabled
     */
    public static boolean isLenientEnabled() {
        final String lenient = System.getProperty(COMMONS_EXEC_LENIENT, Boolean.TRUE.toString());
        return Boolean.TRUE.toString().equalsIgnoreCase(lenient);
    }

}
