package com.jn.agileway.web.rest;

import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.text.i18n.I18nMessageStorageAware;
import com.jn.langx.util.Strings;

import java.util.Locale;

public class I18nRestErrorMessageHandler implements RestErrorMessageHandler, I18nMessageStorageAware {
    private I18nMessageStorage storage;

    @Override
    public void setMessageStorageAware(I18nMessageStorage storage) {
        this.storage = storage;
    }

    @Override
    public void handler(Locale locale, RestRespBody restRespBody) {
        if (Strings.isBlank(restRespBody.getErrorMessage())) {
            String errorCode = restRespBody.getErrorCode();
            String message = storage.getMessage(locale, errorCode);
            restRespBody.setErrorMessage(message);
        }
    }
}
