package com.jn.agileway.shell;

import com.jn.agileway.shell.command.CommandRegistry;
import com.jn.agileway.shell.command.CommandsSupplier;
import com.jn.agileway.shell.factory.CompoundCommandComponentFactory;
import com.jn.langx.environment.Environment;
import org.apache.commons.cli.CommandLineParser;

import java.util.List;

public class Shell {
    protected CommandRegistry commandRegistry;
    protected CompoundCommandComponentFactory componentFactory;
    protected CommandLineParser commandLineParser;

    protected List<CommandsSupplier> commandSuppliers;
    protected Environment environment;

    Shell(){

    }

    public void run(){

    }

}
