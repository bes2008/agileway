package com.jn.agileway.cmd;


import com.jn.langx.util.Strings;
import com.jn.langx.util.io.file.Files;

import java.io.File;
import java.util.Map;

/**
 * Supplement of commons-lang, the stringSubstitution() was in a simpler
 * implementation available in an older commons-lang implementation.
 * <p>
 * This class is not part of the public API and could change without
 * warning.
 */
class CommandLineStrings {
    private CommandLineStrings(){

    }

    /**
     * Perform a series of substitutions.
     * <p>
     * The substitutions are performed by replacing ${variable} in the target string with the value of provided by the
     * key "variable" in the provided hash table.
     * </p>
     * <p>
     * A key consists of the following characters:
     * </p>
     * <ul>
     * <li>letter
     * <li>digit
     * <li>dot character
     * <li>hyphen character
     * <li>plus character
     * <li>underscore character
     * </ul>
     *
     * @param argStr    the argument string to be processed
     * @param vars      name/value pairs used for substitution
     * @param isLenient ignore a key not found in vars or throw a RuntimeException?
     * @return String target string with replacements.
     */
    public static StringBuilder stringSubstitution(final String argStr, final Map<? super String, ?> vars, final boolean isLenient) {

        final StringBuilder argBuf = new StringBuilder();

        if (argStr == null || argStr.length() == 0) {
            return argBuf;
        }

        if (vars == null || vars.size() == 0) {
            return argBuf.append(argStr);
        }

        final int argStrLength = argStr.length();

        for (int cIdx = 0; cIdx < argStrLength; ) {

            char ch = argStr.charAt(cIdx);
            char del = ' ';

            switch (ch) {

                case '$':
                    final StringBuilder nameBuf = new StringBuilder();
                    del = argStr.charAt(cIdx + 1);
                    if (del == '{') {
                        cIdx++;

                        for (++cIdx; cIdx < argStr.length(); ++cIdx) {
                            ch = argStr.charAt(cIdx);
                            if (ch == '_' || ch == '.' || ch == '-' || ch == '+' || Character.isLetterOrDigit(ch)) {
                                nameBuf.append(ch);
                            } else {
                                break;
                            }
                        }

                        if (nameBuf.length() >= 0) {

                            String value;
                            final Object temp = vars.get(nameBuf.toString());

                            if (temp instanceof File) {
                                // for a file we have to fix the separator chars to allow
                                // cross-platform compatibility
                                value = fixFileSeparatorChar(Files.getCanonicalPath((File) temp));
                            } else {
                                value = temp != null ? temp.toString() : null;
                            }

                            if (value != null) {
                                argBuf.append(value);
                            } else {
                                if (isLenient) {
                                    // just append the unresolved variable declaration
                                    argBuf.append("${").append(nameBuf.toString()).append("}");
                                } else {
                                    // complain that no variable was found
                                    throw new RuntimeException("No value found for : " + nameBuf);
                                }
                            }

                            del = argStr.charAt(cIdx);

                            if (del != '}') {
                                throw new RuntimeException("Delimiter not found for : " + nameBuf);
                            }
                        }

                        cIdx++;
                    } else {
                        argBuf.append(ch);
                        ++cIdx;
                    }

                    break;

                default:
                    argBuf.append(ch);
                    ++cIdx;
                    break;
            }
        }

        return argBuf;
    }


    /**
     * Fixes the file separator char for the target platform
     * using the following replacement.
     *
     * <ul>
     *  <li>'/' &#x2192; File.separatorChar</li>
     *  <li>'\\' &#x2192; File.separatorChar</li>
     * </ul>
     *
     * @param arg the argument to fix
     * @return the transformed argument
     */
    public static String fixFileSeparatorChar(final String arg) {
        return arg.replace(Strings.SLASH_CHAR, File.separatorChar).replace(
                Strings.BACKSLASH_CHAR, File.separatorChar);
    }


    /**
     * Put quotes around the given String if necessary.
     * <p>
     * If the argument doesn't include spaces or quotes, return it as is. If it
     * contains double quotes, use single quotes - else surround the argument by
     * double quotes.
     * </p>
     *
     * @param argument the argument to be quoted
     * @return the quoted argument
     * @throws IllegalArgumentException If argument contains both types of quotes
     */
    public static String quoteArgument(final String argument) {

        String cleanedArgument = argument.trim();

        // strip the quotes from both ends
        while (cleanedArgument.startsWith(Strings.SINGLE_QUOTE) || cleanedArgument.startsWith(Strings.DOUBLE_QUOTE)) {
            cleanedArgument = cleanedArgument.substring(1);
        }

        while (cleanedArgument.endsWith(Strings.SINGLE_QUOTE) || cleanedArgument.endsWith(Strings.DOUBLE_QUOTE)) {
            cleanedArgument = cleanedArgument.substring(0, cleanedArgument.length() - 1);
        }

        final StringBuilder buf = new StringBuilder();
        if (cleanedArgument.contains(Strings.DOUBLE_QUOTE)) {
            if (cleanedArgument.contains(Strings.SINGLE_QUOTE)) {
                throw new IllegalArgumentException(
                        "Can't handle single and double quotes in same argument");
            }
            return buf.append(Strings.SINGLE_QUOTE).append(cleanedArgument).append(
                    Strings.SINGLE_QUOTE).toString();
        } else if (cleanedArgument.contains(Strings.SINGLE_QUOTE) || cleanedArgument.contains(" ")) {
            return buf.append(Strings.DOUBLE_QUOTE).append(cleanedArgument).append(
                    Strings.DOUBLE_QUOTE).toString();
        } else {
            return cleanedArgument;
        }
    }

    /**
     * Determines if this is a quoted argument - either single or
     * double quoted.
     *
     * @param argument the argument to check
     * @return true when the argument is quoted
     */
    public static boolean isQuoted(final String argument) {
        return argument.startsWith(Strings.SINGLE_QUOTE) && argument.endsWith(Strings.SINGLE_QUOTE)
                || argument.startsWith(Strings.DOUBLE_QUOTE) && argument.endsWith(Strings.DOUBLE_QUOTE);
    }
}