package com.jn.agileway.shell.command.annotation;

public @interface Command {
    String name() default "";
    String[] alias() default {};

    String desc() default "";
}
