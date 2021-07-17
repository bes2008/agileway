package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.langx.util.function.Function2;

import java.util.List;
/**
 * @since 2.6.0
 */
public interface StubProviderLocator extends Function2<List<SimpleStubProvider>, Class, String> {
    @Override
    String apply(List<SimpleStubProvider> providers, Class stubClass);
}
