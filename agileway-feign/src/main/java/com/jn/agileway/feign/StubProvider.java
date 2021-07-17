package com.jn.agileway.feign;

public interface StubProvider {
    <Stub> Stub getStub(Class<Stub> stubInterface);
}
