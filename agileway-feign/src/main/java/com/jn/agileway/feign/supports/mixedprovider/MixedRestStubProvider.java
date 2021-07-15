package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.RestServiceProvider;
import com.jn.agileway.feign.RestStubProvider;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;

import java.util.HashMap;
import java.util.List;

public class MixedRestStubProvider extends GenericRegistry<RestServiceProvider> implements RestStubProvider {

    private RestStubProviderLocator locator;


    public MixedRestStubProvider() {
        super(new HashMap<String, RestServiceProvider>());
    }

    public void setLocator(RestStubProviderLocator locator) {
        this.locator = locator;
    }

    @Override
    public <Stub> Stub getStub(Class<Stub> stubInterface) {
        List<RestServiceProvider> providers = instances();
        Preconditions.checkNotEmpty(providers, "has no any restful stub provider, please register them");
        if (providers.size() == 1) {
            return providers.get(0).getService(stubInterface);
        }
        String providerName = locator.apply(providers, stubInterface);
        Preconditions.checkNotEmpty(providerName, "Can't find any valid restful stub provider for stub [{}]", Reflects.getFQNClassName(stubInterface));
        RestServiceProvider provider = get(providerName);
        Preconditions.checkNotNull(provider, "Can't find a restful stub provider named [{}]", providerName);
        return provider.getService(stubInterface);
    }
}
