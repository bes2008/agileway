package com.jn.agileway.shell.command.annotation;

public @interface CommandGroup {
    String name() default "";
    String desc() default "";
}
