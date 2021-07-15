package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.RestServiceProvider;
import com.jn.langx.util.function.Function2;

import java.util.Collection;
import java.util.List;

public interface RestStubProviderLocator extends Function2<List<RestServiceProvider>, Class, String> {
    @Override
    String apply(List<RestServiceProvider> providers, Class stubClass);
}
