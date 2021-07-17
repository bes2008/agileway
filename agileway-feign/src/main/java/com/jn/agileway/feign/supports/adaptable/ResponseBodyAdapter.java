package com.jn.agileway.feign.supports.adaptable;

import feign.Response;

import java.lang.reflect.Type;

/**
 * 当decoder#decode后，如果返回值与期望的不符时，可以使用该适配器进行适配
 * @since 2.6.0
 */
public interface ResponseBodyAdapter {
    Object adapt(Response response, Type expectedType, Object decodedObject);
}
