package com.jn.agileway.shell.command.annotation;

import com.jn.agileway.shell.result.RawTextOutputTransformer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.PACKAGE})
public @interface CommandGroup {
    String value() default "";
    String desc() default "";
    Class outputTransformer() default RawTextOutputTransformer.class;
}
