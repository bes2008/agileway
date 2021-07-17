package com.jn.agileway.feign;

import com.jn.langx.Customizer;

/**
 * 提供自定义功能
 *
 * @since 2.6.0
 */
public interface SimpleStubProviderCustomizer extends Customizer<SimpleStubProvider> {
    @Override
    void customize(SimpleStubProvider stubProvider);
}
