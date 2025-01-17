package com.jn.agileway.shell.exec;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.langx.environment.Environment;
import org.apache.commons.cli.CommandLineParser;

public class Shell {
    protected CommandRegistry commandRegistry;
    protected CompoundCommandComponentFactory componentFactory;
    protected CommandLineParser commandLineParser;

    protected Environment environment;

    Shell(){

    }


}
