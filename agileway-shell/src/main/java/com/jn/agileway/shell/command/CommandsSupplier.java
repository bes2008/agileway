package com.jn.agileway.shell.command;

import com.jn.langx.environment.Environment;
import com.jn.langx.util.function.Supplier;

import java.util.List;
import java.util.Map;

public interface CommandsSupplier extends Supplier<Environment, Map<CommandGroup, List<Command>>> {
    Map<CommandGroup, List<Command>> get(Environment environment);
}
