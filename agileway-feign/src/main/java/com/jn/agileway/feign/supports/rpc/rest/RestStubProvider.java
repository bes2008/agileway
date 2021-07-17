package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.lifecycle.InitializationException;

/**
 * @see SimpleStubProvider
 */
@Deprecated
public class RestStubProvider extends SimpleStubProvider {
    private JSONFactory jsonFactory;
    private Class unifiedRestRestResponseClass;

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void setUnifiedRestRestResponseClass(Class unifiedRestRestResponseClass) {
        this.unifiedRestRestResponseClass = unifiedRestRestResponseClass;
    }

    @Override
    public void doInit() throws InitializationException {
        if (this.customizer == null) {
            RestStubProviderCustomizer customizer = new RestStubProviderCustomizer();
            customizer.setJsonFactory(jsonFactory);
            setCustomizer(customizer);
        } else {
            if (this.customizer instanceof RestStubProviderCustomizer) {
                RestStubProviderCustomizer customizer = new RestStubProviderCustomizer();
                customizer.setJsonFactory(jsonFactory);
            }
        }
        super.doInit();
    }
}
