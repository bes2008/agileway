package com.jn.agileway.shell.command.annotation;

/**
 * <pre>
 * command [&lt;options&gt;] [&lt;arguments&gt;]
 * 1. 只有方法的最后N个参数可以作为 arguments 部分来使用，也是就是说该注解参数只能用在方法的最后几个参数上
 * 2. arguments 的顺序 就是按照在方法中出现的顺序来控制的。
 * 3. arguments 中（ M 个），只有最后的 N 个（N <= M） 可以是可选的。
 * 4. 要作为arguments的方法参数，必须使用该注解。
 * 5. arguments 的数据类型，最后一个参数可以是String[]或者String，其它的只能是String的，需要其它的类型时，可自行在方法中转换
 * </pre>
 *
 *
 */
public @interface CommandArgument {
    /**
     * 参数名称，用于显示
     */
    String value();

    boolean required() default true;

    String desc() default "";
}
