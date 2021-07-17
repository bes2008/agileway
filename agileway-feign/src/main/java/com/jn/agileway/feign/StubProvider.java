package com.jn.agileway.feign;

/**
 * 服务Stub提供者
 * @since 2.6.0
 */
public interface StubProvider {
    <Stub> Stub getStub(Class<Stub> stubInterface);
}
