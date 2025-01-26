package com.jn.agileway.shell.command.annotation;

import com.jn.agileway.shell.command.Commands;
import com.jn.agileway.shell.command.DefaultConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface CommandOption {
    /**
     * 选项的长称
     * @return string
     */
    String value() default "";

    /**
     * 选项的短名
     */
    String shortName() default "";
    /**
     * 只针对 boolean 、Boolean 类型的数据有用
     * @return
     */
    boolean isFlag() default false;

    String argName() default "";

    /**
     * 值转换器
     */
    Class converter() default DefaultConverter.class;


    /**
     * 默认值
     */
    String defaultValue() default Commands.NULL;

    /**
     * 值分隔符
     */
    char valueSeparator() default ',';

    String desc() default "";

}
