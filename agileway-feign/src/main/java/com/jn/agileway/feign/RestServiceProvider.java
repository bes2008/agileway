package com.jn.agileway.feign;

import com.jn.agileway.feign.supports.rpc.rest.RestStubProvider;


/**
 * @see SimpleStubProvider
 * @since 1.0.0
 */
@Deprecated
public class RestServiceProvider extends RestStubProvider {

    public <Service> Service getService(Class<Service> serviceInterface) {
        return this.getStub(serviceInterface);
    }

}
