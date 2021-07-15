package com.jn.agileway.feign;

public interface RestStubProvider {
    <Stub> Stub getStub(Class<Stub> stubInterface);
}
