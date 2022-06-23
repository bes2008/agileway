package com.jn.agileway.http.rest;

import com.jn.langx.annotation.NonNull;
import com.jn.langx.http.rest.RestRespBody;
import com.jn.langx.text.i18n.I18nMessageStorage;
import com.jn.langx.text.i18n.I18nMessageStorageAware;
import com.jn.langx.util.Emptys;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.function.Predicate;
import com.jn.langx.util.logging.Loggers;
import org.slf4j.Logger;

import java.util.Locale;

public class I18nRestErrorMessageHandler implements RestErrorMessageHandler, I18nMessageStorageAware {
    private static final Logger logger = Loggers.getLogger(I18nRestErrorMessageHandler.class);
    private I18nMessageStorage storage;

    private I18nRestErrorMessageHandlerProperties config = new I18nRestErrorMessageHandlerProperties();

    @Override
    public void setI18nMessageStorage(I18nMessageStorage i18nMessageStorage) {
        this.storage = i18nMessageStorage;
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
        errorCode = Objs.<String>useValueIfMatch(errorCode, new Predicate<String>() {
            @Override
            public boolean test(String s) {
                return Strings.equalsIgnoreCase("UNKNOWN", s);
            }
        }, "");
        if (Emptys.isEmpty(errorCode)) {
            if (restRespBody.getStatusCode() >= 400) {
                errorCode = "HTTP-" + restRespBody.getStatusCode();
            }
        }

        String message = restRespBody.getErrorMessage();
        if(Strings.isBlank(message)) {
            try {
                message = storage.getMessage(locale, I18nRestErrorMessageHandler.class.getClassLoader(), errorCode, restRespBody.getErrorMessage());
            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
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
