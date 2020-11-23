package com.jn.agileway.web.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Strings;

import java.util.Locale;

public class I18nRestErrorMessageHandler implements RestErrorMessageHandler {
    private I18nMessageStorage storage;
    /**
     * 是否强制使用 I18n storage里提供的错误消息
     */
    private boolean forceI18n = true;

    public void setI18MessageStorage(I18nMessageStorage storage) {
        this.storage = storage;
    }

    public void setForceI18n(boolean forceI18n) {
        this.forceI18n = forceI18n;
    }

    @Override
    public void handler(@NonNull Locale locale, @NonNull RestRespBody restRespBody) {
        if (forceI18n) {
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
}
