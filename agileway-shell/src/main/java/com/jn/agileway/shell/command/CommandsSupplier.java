package com.jn.agileway.shell.command;

import com.jn.langx.util.function.Supplier0;

import java.util.List;

public interface CommandsSupplier extends Supplier0<List<Command>> {
    @Override
    List<Command> get();
}
