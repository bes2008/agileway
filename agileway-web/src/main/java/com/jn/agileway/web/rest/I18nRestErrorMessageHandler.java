package com.jn.agileway.web.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;

import java.util.Locale;

public class I18nRestErrorMessageHandler implements RestErrorMessageHandler {
    private I18nMessageStorage storage;

    private I18nRestErrorMessageHandlerProperties config = new I18nRestErrorMessageHandlerProperties();

    public void setI18MessageStorage(I18nMessageStorage storage) {
        this.storage = storage;
    }

    @Override
    public void handler(@NonNull Locale locale, @NonNull RestRespBody restRespBody) {
        if (config.isForceI18n()) {
            useI18nMessage(locale, restRespBody);
        } else if (Strings.isBlank(restRespBody.getErrorMessage())) {
            useI18nMessage(locale, restRespBody);
        }
    }

    private void useI18nMessage(@NonNull Locale locale, @NonNull RestRespBody restRespBody) {
        String errorCode = restRespBody.getErrorCode();
        if (Emptys.isEmpty(errorCode)) {
            if (restRespBody.getStatusCode() >= 400) {
                errorCode = "HTTP-" + restRespBody.getStatusCode();
            }
        }
        String message = storage.getMessage(locale, errorCode);
        if (Emptys.isNotEmpty(message)) {
            restRespBody.setErrorCode(errorCode);
            restRespBody.setErrorMessage(message);
        }
    }

    public void setConfig(I18nRestErrorMessageHandlerProperties config) {
        if (config != null) {
            this.config = config;
        }
    }
}
