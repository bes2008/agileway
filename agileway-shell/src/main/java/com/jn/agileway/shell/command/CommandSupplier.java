package com.jn.agileway.shell.command;

import com.jn.langx.util.function.Supplier0;

import java.util.List;
import java.util.Map;

public interface CommandSupplier extends Supplier0<Map<CommandGroup, List<Command>>> {
    Map<CommandGroup, List<Command>> get();
}
