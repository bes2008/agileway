package com.jn.agileway.feign.supports.rpc.rest;

import com.jn.agileway.feign.ErrorHandler;
import com.jn.agileway.feign.SimpleStubProvider;
import com.jn.agileway.feign.SimpleStubProviderCustomizer;
import com.jn.easyjson.core.JSONFactory;
import com.jn.easyjson.core.factory.JsonFactorys;
import com.jn.easyjson.core.factory.JsonScope;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.FormEncoder;

/**
 * @since 2.6.0
 */
public class RestStubProviderCustomizer implements SimpleStubProviderCustomizer {
    private JSONFactory jsonFactory = JsonFactorys.getJSONFactory(JsonScope.SINGLETON);

    public void setJsonFactory(JSONFactory jsonFactory) {
        if (jsonFactory != null) {
            this.jsonFactory = jsonFactory;
        }
    }

    public RestStubProviderCustomizer() {

    }

    public RestStubProviderCustomizer(JSONFactory jsonFactory) {
        setJsonFactory(jsonFactory);
    }

    @Override
    public void customize(SimpleStubProvider stubProvider) {
        Encoder encoder = stubProvider.getEncoder();
        if (encoder == null) {
            encoder = new FormEncoder(new EasyjsonEncoder(jsonFactory));
            stubProvider.setEncoder(encoder);
        }

        ErrorDecoder errorDecoder = stubProvider.getErrorDecoder();
        if (errorDecoder == null) {
            errorDecoder = new EasyjsonErrorDecoder();
            stubProvider.setErrorDecoder(errorDecoder);
        }

        Decoder decoder = stubProvider.getDecoder();
        if (decoder == null) {
            decoder = new EasyjsonDecoder(jsonFactory);
            stubProvider.setDecoder(decoder);
        }

        ErrorHandler errorHandler = stubProvider.getErrorHandler();
        if (errorHandler == null) {
            UnifiedResponseRestErrorHandler handler = new UnifiedResponseRestErrorHandler();
            stubProvider.setErrorHandler(handler);
        }

    }
}
