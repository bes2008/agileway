package com.jn.agileway.feign.supports.mixedprovider;

import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.agileway.feign.StubProvider;
import com.jn.langx.registry.GenericRegistry;
import com.jn.langx.util.Preconditions;
import com.jn.langx.util.reflect.Reflects;

import java.util.HashMap;
import java.util.List;

public class MixedStubProvider extends GenericRegistry<SimpleStubProvider> implements StubProvider {

    private StubProviderLocator locator;


    public MixedStubProvider() {
        super(new HashMap<String, SimpleStubProvider>());
    }

    public void setLocator(StubProviderLocator locator) {
        this.locator = locator;
    }

    @Override
    public <Stub> Stub getStub(Class<Stub> stubInterface) {
        List<SimpleStubProvider> providers = instances();
        Preconditions.checkNotEmpty(providers, "has no any restful stub provider, please register them");
        if (providers.size() == 1) {
            return providers.get(0).getStub(stubInterface);
        }
        String providerName = locator.apply(providers, stubInterface);
        Preconditions.checkNotEmpty(providerName, "Can't find any valid restful stub provider for stub [{}]", Reflects.getFQNClassName(stubInterface));
        SimpleStubProvider provider = get(providerName);
        Preconditions.checkNotNull(provider, "Can't find a restful stub provider named [{}]", providerName);
        return provider.getStub(stubInterface);
    }
}
