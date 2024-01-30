package com.jn.agileway.audit.core.annotation;

import com.jn.agileway.audit.core.model.Resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 用在一个Entity类上，用于标注 分别代表 id, name的字段名
 *
 * 用在方法上，id,name 分别代表 resourceId,resourceName所对应的参数名称
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, PARAMETER, METHOD})
public @interface ResourceMapping {
    /**
     * @see com.jn.agileway.audit.core.model.Resource resourceName
     */
    String name() default Resource.RESOURCE_NAME_DEFAULT_KEY;

    /**
     * @see com.jn.agileway.audit.core.model.Resource resourceId
     */
    String id() default Resource.RESOURCE_ID_DEFAULT_KEY;

    /**
     * @see com.jn.agileway.audit.core.model.Resource resourceType
     */
    String type() default "";
}
