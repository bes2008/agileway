package com.jn.agileway.shell.command.annotation;

/**
 * <pre>
 * 该注解有两个作用：
 * 1. 用在一个 Command 的方法上，表示该 Command 的可用性是哪个方法。
 * 2. 用在一个 public Availability xxx() 的方法上，表示该 Availability 方法可用于哪些 commands.
 *      如果 value() 是空，就代表可用于 当前 command group下所有的commands
 *      如果 value() 不是空，就代表可用于 value()指定的 commands
 * </pre>
 */
public @interface CommandAvailability {
    String[] value() default {};
}
