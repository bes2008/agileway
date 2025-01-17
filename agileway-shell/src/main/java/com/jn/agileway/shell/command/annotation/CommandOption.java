package com.jn.agileway.shell.command.annotation;

import com.jn.agileway.shell.command.DefaultConverter;
import com.jn.langx.annotation.Nullable;

/**
 * example:
 * <pre>
 * ant [options] [target [target2 [target3] ...]]
 * Options:
 * -help  print this message
 * -projecthelp  print project help information
 * -version  print the version information and exit
 * -quiet be extra quiet
 * -verbose be extra verbose
 * -debug print debugging information
 * -emacs produce logging information without adornments
 * -logfile file use given file for log output
 * -logger classname the class that is to perform logging
 * -listener classname add an instance of class as a project listener
 * -buildfile file use specified buildfile
 * -find file search for buildfile towards the root of the filesystem and use the first one found
 * -Dproperty=value set property to value
 * </pre>
 */
public @interface CommandOption {
    /**
     * 选项短名称
     * @return string
     */
    String value() default "";

    /**
     * 选项的长名
     */
    String longName() default "";

    /**
     * 选项是否必须出现在命令行上
     */
    boolean required() default true;

    /**
     * 选项是否有一个参数的
     */
    boolean hasArg() default true;

    /**
     * 选项是否支持多个参数，或者可理解为该选项在命令行字符串上是否可以出现多次。
     */
    boolean hasArgs() default false;

    @Nullable
    String argName() default "";


    boolean argOptional() default false;

    /**
     * 选项值的数据类型
     */
    Class type() default String.class;

    /**
     * 值的类型
     */
    Class converter() default DefaultConverter.class;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     *
     */
    char valueSeparator() default ',';

    String desc() default "";

}
