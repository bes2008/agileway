package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.easyjson.core.JSONFactory;
import com.jn.langx.lifecycle.InitializationException;

/**
 * @see SimpleStubProvider
 * @since 2.6.0
 */
@Deprecated
public class RestStubProvider extends SimpleStubProvider {
    private JSONFactory jsonFactory;
    private Class unifiedRestResponseClass;

    public void setJsonFactory(JSONFactory jsonFactory) {
        this.jsonFactory = jsonFactory;
    }

    public void setUnifiedRestResponseClass(Class unifiedRestResponseClass) {
        this.unifiedRestResponseClass = unifiedRestResponseClass;
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
