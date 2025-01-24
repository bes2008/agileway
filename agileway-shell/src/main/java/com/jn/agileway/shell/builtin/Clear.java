package com.jn.agileway.shell.builtin;

import com.jn.agileway.shell.command.annotation.Command;
import com.jn.agileway.shell.command.annotation.CommandComponent;

@CommandComponent
public class Clear {
    @Command(value = "clear", alias = {"cls","clean"}, desc = "clear screen")
    public void clear(){
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}
