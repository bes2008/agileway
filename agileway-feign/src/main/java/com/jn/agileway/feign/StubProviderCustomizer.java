package com.jn.agileway.feign;

import com.jn.langx.Customizer;

public interface StubProviderCustomizer extends Customizer<SimpleStubProvider> {
    @Override
    void customize(SimpleStubProvider stubProvider);
}
