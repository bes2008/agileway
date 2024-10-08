package com.jn.agileway.cmd;

import com.jn.langx.text.StrTokenizer;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Lists;
import com.jn.langx.util.collection.Maps;
import com.jn.langx.util.io.file.Files;

import java.io.File;
import java.util.*;


/**
 * CommandLine objects help handling command lines specifying processes to
 * execute. The class can be used to a command line by an application.
 */
public class CommandLine {

    /**
     * The arguments of the command.
     */
    private final List<Argument> arguments = Lists.<Argument>newArrayList();

    /**
     * The program to execute.
     */
    private final String executable;

    /**
     * A map of name value pairs used to expand command line arguments
     */
    private Map<String, ?> substitutionMap; // N.B. This can contain values other than Strings

    /**
     * Was a file being used to set the executable?
     */
    private final boolean isFile;

    /**
     * Create a command line from a string.
     *
     * @param line the first element becomes the executable, the rest the arguments
     * @return the parsed command line
     * @throws IllegalArgumentException If line is null or all whitespace
     */
    public static CommandLine parse(final String line) {
        return parse(line, null);
    }

    /**
     * Create a command line from a string.
     *
     * @param line            the first element becomes the executable, the rest the arguments
     * @param substitutionMap the name/value pairs used for substitution
     * @return the parsed command line
     * @throws IllegalArgumentException If line is null or all whitespace
     */
    public static CommandLine parse(final String line, final Map<String, ?> substitutionMap) {

        if (line == null) {
            throw new IllegalArgumentException("Command line can not be null");
        } else if (line.trim().length() == 0) {
            throw new IllegalArgumentException("Command line can not be empty");
        } else {
            final String[] tmp = translateCommandline(line);

            final CommandLine cl = new CommandLine(tmp[0]);
            cl.setSubstitutionMap(substitutionMap);
            for (int i = 1; i < tmp.length; i++) {
                cl.addArgument(tmp[i]);
            }

            return cl;
        }
    }

    /**
     * Create a command line without any arguments.
     *
     * @param executable the executable
     */
    public CommandLine(final String executable) {
        this.isFile = false;
        this.executable = toCleanExecutable(executable);
    }

    /**
     * Create a command line without any arguments.
     *
     * @param executable the executable file
     */
    public CommandLine(final File executable) {
        this.isFile = true;
        this.executable = toCleanExecutable(Files.getCanonicalPath(executable));
    }

    /**
     * Copy constructor.
     *
     * @param other the instance to copy
     */
    public CommandLine(final CommandLine other) {
        this.executable = other.getExecutable();
        this.isFile = other.isFile();
        this.arguments.addAll(other.arguments);

        if (other.getSubstitutionMap() != null) {
            final Map<String, Object> omap = Maps.<String, Object>newHashMap(other.substitutionMap.size());
            this.substitutionMap = omap;
            for (String key : other.substitutionMap.keySet()) {
                omap.put(key, other.getSubstitutionMap().get(key));
            }
        }
    }

    /**
     * Returns the executable.
     *
     * @return The executable
     */
    public String getExecutable() {
        // Expand the executable and replace '/' and '\\' with the platform
        // specific file separator char. This is safe here since we know
        // that this is a platform specific command.
        return CommandLineStrings.fixFileSeparatorChar(expandArgument(executable));
    }

    /**
     * Was a file being used to set the executable?
     *
     * @return true if a file was used for setting the executable
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Add multiple arguments. Handles parsing of quotes and whitespace.
     *
     * @param addArguments An array of arguments
     * @return The command line itself
     */
    public CommandLine addArguments(final String[] addArguments) {
        return this.addArguments(addArguments, true);
    }

    /**
     * Add multiple arguments.
     *
     * @param addArguments  An array of arguments
     * @param handleQuoting Add the argument with/without handling quoting
     * @return The command line itself
     */
    public CommandLine addArguments(final String[] addArguments, final boolean handleQuoting) {
        if (addArguments != null) {
            for (final String addArgument : addArguments) {
                addArgument(addArgument, handleQuoting);
            }
        }

        return this;
    }

    /**
     * Add multiple arguments. Handles parsing of quotes and whitespace.
     * Please note that the parsing can have undesired side-effects therefore
     * it is recommended to build the command line incrementally.
     *
     * @param addArguments An string containing multiple arguments.
     * @return The command line itself
     */
    public CommandLine addArguments(final String addArguments) {
        return this.addArguments(addArguments, true);
    }

    /**
     * Add multiple arguments. Handles parsing of quotes and whitespace.
     * Please note that the parsing can have undesired side-effects therefore
     * it is recommended to build the command line incrementally.
     *
     * @param addArguments  An string containing multiple arguments.
     * @param handleQuoting Add the argument with/without handling quoting
     * @return The command line itself
     */
    public CommandLine addArguments(final String addArguments, final boolean handleQuoting) {
        if (addArguments != null) {
            final String[] argumentsArray = translateCommandline(addArguments);
            addArguments(argumentsArray, handleQuoting);
        }

        return this;
    }

    /**
     * Add a single argument. Handles quoting.
     *
     * @param argument The argument to add
     * @return The command line itself
     * @throws IllegalArgumentException If argument contains both single and double quotes
     */
    public CommandLine addArgument(final String argument) {
        return this.addArgument(argument, true);
    }

    /**
     * Add a single argument.
     *
     * @param argument      The argument to add
     * @param handleQuoting Add the argument with/without handling quoting
     * @return The command line itself
     */
    public CommandLine addArgument(final String argument, final boolean handleQuoting) {

        if (argument == null) {
            return this;
        }

        // check if we can really quote the argument - if not throw an
        // IllegalArgumentException
        if (handleQuoting) {
            CommandLineStrings.quoteArgument(argument);
        }

        arguments.add(new Argument(argument, handleQuoting));
        return this;
    }

    /**
     * Returns the expanded and quoted command line arguments.
     *
     * @return The quoted arguments
     */
    public String[] getArguments() {

        Argument currArgument;
        String expandedArgument;
        final String[] result = new String[arguments.size()];

        for (int i = 0; i < result.length; i++) {
            currArgument = arguments.get(i);
            expandedArgument = expandArgument(currArgument.getValue());
            result[i] = currArgument.isHandleQuoting() ? CommandLineStrings.quoteArgument(expandedArgument) : expandedArgument;
        }

        return result;
    }

    /**
     * @return the substitution map
     */
    public Map<String, ?> getSubstitutionMap() {
        return substitutionMap;
    }

    /**
     * Set the substitutionMap to expand variables in the
     * command line.
     *
     * @param substitutionMap the map
     */
    public void setSubstitutionMap(final Map<String, ?> substitutionMap) {
        this.substitutionMap = substitutionMap;
    }

    /**
     * Returns the command line as an array of strings.
     *
     * @return The command line as an string array
     */
    public String[] toStrings() {
        final String[] result = new String[arguments.size() + 1];
        result[0] = this.getExecutable();
        System.arraycopy(getArguments(), 0, result, 1, result.length - 1);
        return result;
    }

    /**
     * Stringify operator returns the command line as a string.
     * Parameters are correctly quoted when containing a space or
     * left untouched if the are already quoted.
     *
     * @return the command line as single string
     */
    @Override
    public String toString() {
        return "[" + Strings.join(", ", toStrings()) + "]";
    }

    public String getCommandLineString() {
        String[] strings = toStrings();
        return Strings.join(" ", strings);
    }

    // --- Implementation ---------------------------------------------------

    /**
     * Expand variables in a command line argument.
     *
     * @param argument the argument
     * @return the expanded string
     */
    private String expandArgument(final String argument) {
        return CommandLineStrings.stringSubstitution(argument, this.getSubstitutionMap(), true).toString();
    }

    /**
     * Crack a command line.
     *
     * @param toProcess the command line to process
     * @return the command line broken into strings. An empty or null toProcess
     * parameter results in a zero sized array
     */
    private static String[] translateCommandline(final String toProcess) {
        if (Strings.isEmpty(toProcess)) {
            // no command? no string
            return new String[0];
        }

        // parse with a simple finite state machine

        final int normal = 0;
        final int inQuote = 1;
        final int inDoubleQuote = 2;
        int state = normal;
        final StrTokenizer tok = new StrTokenizer(toProcess,  true,"\"","'"," ");
        final ArrayList<String> list = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean lastTokenHasBeenQuoted = false;

        while (tok.hasNext()) {
            final String nextTok = tok.next();
            switch (state) {
                case inQuote:
                    if (Strings.SINGLE_QUOTE.equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                case inDoubleQuote:
                    if ("\"".equals(nextTok)) {
                        lastTokenHasBeenQuoted = true;
                        state = normal;
                    } else {
                        current.append(nextTok);
                    }
                    break;
                default:
                    if (Strings.SINGLE_QUOTE.equals(nextTok)) {
                        state = inQuote;
                    } else if ("\"".equals(nextTok)) {
                        state = inDoubleQuote;
                    } else if (" ".equals(nextTok)) {
                        if (lastTokenHasBeenQuoted || current.length() != 0) {
                            list.add(current.toString());
                            current = new StringBuilder();
                        }
                    } else {
                        current.append(nextTok);
                    }
                    lastTokenHasBeenQuoted = false;
                    break;
            }
        }

        if (lastTokenHasBeenQuoted || current.length() != 0) {
            list.add(current.toString());
        }

        if (state == inQuote || state == inDoubleQuote) {
            throw new IllegalArgumentException("Unbalanced quotes in "
                    + toProcess);
        }

        final String[] args = new String[list.size()];
        return list.toArray(args);
    }

    /**
     * Cleans the executable string. The argument is trimmed and '/' and '\\' are
     * replaced with the platform specific file separator char
     *
     * @param dirtyExecutable the executable
     * @return the platform-specific executable string
     */
    private String toCleanExecutable(final String dirtyExecutable) {
        if (dirtyExecutable == null) {
            throw new IllegalArgumentException("Executable can not be null");
        } else if (dirtyExecutable.trim().length() == 0) {
            throw new IllegalArgumentException("Executable can not be empty");
        } else {
            return CommandLineStrings.fixFileSeparatorChar(dirtyExecutable);
        }
    }

    /**
     * Encapsulates a command line argument.
     */
    static class Argument {

        private final String value;
        private final boolean handleQuoting;

        private Argument(final String value, final boolean handleQuoting) {
            this.value = value.trim();
            this.handleQuoting = handleQuoting;
        }

        private String getValue() {
            return value;
        }

        private boolean isHandleQuoting() {
            return handleQuoting;
        }
    }
}
