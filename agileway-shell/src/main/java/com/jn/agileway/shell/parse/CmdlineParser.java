package com.jn.agileway.shell.parse;

import com.jn.agileway.shell.command.Command;
import com.jn.agileway.shell.exception.MalformedCommandException;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.Collects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

public class CmdlineParser{
    /**
     * 解析命令行时，遇到了一个option时，如果选项是未知的，是否停止解析。
     * 如果停止解析，那么后续的字符串都将作为args
     * 如果不停止，就会抛出UnrecognizedOptionException
     */
    private boolean stopParseAtNonDefinedOption;

    public CmdlineParser(boolean stopParseAtNonDefinedOption){
        this.stopParseAtNonDefinedOption = stopParseAtNonDefinedOption;
    }

    public Cmdline parse(Command commandDef, String[] cmdline) throws MalformedCommandException {
        try {
            int cmdArgsOffset = Strings.splitRegexp(commandDef.getName(), "\\s+").length;
            String[] cmdArgs = Collects.<String>skip(cmdline, cmdArgsOffset);
            CommandLine parsedCommandLine = new DefaultParser().parse(commandDef.getOptions(), cmdArgs, stopParseAtNonDefinedOption);
            Cmdline parsedCmdline = new Cmdline(commandDef, parsedCommandLine);
            return parsedCmdline;
        }catch (ParseException e){
            throw new MalformedCommandException(e);
        }
    }
}
