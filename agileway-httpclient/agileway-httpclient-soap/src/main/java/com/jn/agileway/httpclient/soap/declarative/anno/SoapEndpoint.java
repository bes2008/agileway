package com.jn.agileway.httpclient.soap.declarative.anno;

import com.jn.agileway.httpclient.soap.entity.SoapBinding;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(value = {TYPE})
public @interface SoapEndpoint {
    /**
     * soap endpoint uri
     *
     * @return the uri for a soap endpoint
     */
    String value() default "";

    SoapBinding binding() default SoapBinding.SOAP12_HTTP;
}
